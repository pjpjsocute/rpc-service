package org.example.ray.infrastructure.loadbalance.impl;

import static org.example.ray.constants.RpcConstants.VIRTUAL_NODES;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.example.ray.domain.RpcRequest;
import org.example.ray.infrastructure.loadbalance.LoadBalanceService;
import org.example.ray.enums.LoadBalanceType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description: The core idea of the consistent hashing algorithm is to map
 *               each service address to a location on the hash ring via a hash
 *               function, then map the request to the hash ring via a hash
 *               function, and finally select the service address that is
 *               closest to the request in a clockwise direction on the hash
 *               ring. In this way, even if the set of service addresses
 *               changes, it only affects a small number of locations on the
 *               hash ring, thus achieving load balancing.
 */
@Component
@Slf4j
public class ConsistentHashLoadBalanceService implements LoadBalanceService {

    private final Map<String, ConsistentHashLoadBalanceSelector> serviceToSelectorMap = new ConcurrentHashMap<>();

    private static class ConsistentHashLoadBalanceSelector {
        // hash to virtual node list
        private final TreeMap<Long, String> virtualInvokers;

        private ConsistentHashLoadBalanceSelector(List<String> serviceUrlList, int virtualNodeNumber) {
            this.virtualInvokers = new TreeMap<>();
            // generate service address virtual node]
            // one address may map to multiple virtual nodes
            // use the md5 hash algorithm to generate the hash value of the
            // virtual node
            for (String serviceNode : serviceUrlList) {
                addVirtualNode(serviceNode, virtualNodeNumber);
            }

        }

        private void addVirtualNode(String serviceNode, int virtualNodeNumber) {
            for (int i = 0; i < virtualNodeNumber / 8; i++) {
                String virtualNodeName = serviceNode + "#" + i;
                byte[] md5Hash = md5Hash(virtualNodeName);
                // md5Hash have 32 bytes
                // use 8 byte for each virtual node
                for (int j = 0; j < 4; j++) {
                    Long hash = calculateHash(md5Hash, j);
                    virtualInvokers.put(hash, virtualNodeName);
                }
            }
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5Hash(rpcServiceKey);
            //use first 8 byte to get hash
            return selectForKey(calculateHash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }

    }

    protected static byte[] md5Hash(String input) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            messageDigest.update(hashBytes);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    protected static Long calculateHash(byte[] digest, int idx) {
        if (digest.length < (idx + 1) * 8) {
            throw new IllegalArgumentException("Insufficient length of digest");
        }

        long hash = 0;
        // 8 bytes digest,a byte is 8 bits like :1321 2432
        // each loop choose a byte to calculate hash,and shift i*8 bits
        for (int i = 0; i < 8; i++) {
            hash |= (255L & (long)digest[i + idx * 8]) << (8 * i);
        }
        return hash;
    }

    @Override
    public LoadBalanceType fetchLoadBalanceType() {
        return LoadBalanceType.HASH;
    }

    /**
     * Choose one from the list of existing service addresses list
     * 
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return
     */
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        int serviceListHash = System.identityHashCode(serviceUrlList);
        String interfaceName = rpcRequest.getServiceName();
        String selectorKey = interfaceName + serviceListHash;

        ConsistentHashLoadBalanceSelector consistentHashLoadBalanceSelector = serviceToSelectorMap
            .computeIfAbsent(selectorKey, key -> new ConsistentHashLoadBalanceSelector(serviceUrlList, VIRTUAL_NODES));

        return consistentHashLoadBalanceSelector.select(interfaceName + Arrays.stream(rpcRequest.getParameters()));
    }

}
