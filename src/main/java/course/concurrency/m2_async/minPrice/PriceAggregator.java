package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice1(long itemId) {
        List<CompletableFuture<Double>> futures = shopIds.stream()
                .map(shopId -> CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executorService)
                        .orTimeout(2900, TimeUnit.MILLISECONDS)
                        .exceptionally(e -> Double.NaN))
                .toList();
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        return futures.stream().mapToDouble(CompletableFuture::join).filter(Double::isFinite).min().orElse(Double.NaN);
    }

    public double getMinPrice(long itemId) {
        return  shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executorService)
                        .exceptionally(e -> Double.POSITIVE_INFINITY)
                        .completeOnTimeout(Double.POSITIVE_INFINITY,2900, TimeUnit.MILLISECONDS)
                )
                .reduce((f1, f2) -> f1.thenCombine(f2, Double::min))
                .map(CompletableFuture::join)
                .filter(Double::isFinite)
                .orElse(Double.NaN);
    }
}
