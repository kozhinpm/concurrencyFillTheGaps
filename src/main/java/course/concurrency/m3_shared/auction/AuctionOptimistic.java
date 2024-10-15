package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;
    private final AtomicReference<Bid> latestBid;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid = new AtomicReference<>(new Bid(-1L, -1L, -1L));
    }



    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            currentBid = latestBid.get();
            if (bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(currentBid, bid));
        notifier.sendOutdatedMessage(currentBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
