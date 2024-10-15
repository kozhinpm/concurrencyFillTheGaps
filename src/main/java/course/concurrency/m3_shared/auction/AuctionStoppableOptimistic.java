package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid = new AtomicMarkableReference<>(new Bid(-1L, -1L, -1L), false);
    }

    private final AtomicMarkableReference<Bid> latestBid;

    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            if (latestBid.isMarked()) {
                return false;
            }
            currentBid = latestBid.getReference();
            if (currentBid != null && bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(currentBid, bid, false, false));
        notifier.sendOutdatedMessage(currentBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid latest;
        do {
            latest = latestBid.getReference();
        } while (!latestBid.attemptMark(latest, true));
        return latest;
    }
}
