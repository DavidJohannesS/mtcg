package uni.local.services;

import uni.local.models.Trade;
import uni.local.repository.TradeRepository;

import java.util.List;
import java.util.UUID;

public class TradeService {
    private static TradeService instance;
    private final TradeRepository tradeRepository;

    private TradeService() {
        this.tradeRepository = TradeRepository.getInstance();
    }

    public static synchronized TradeService getInstance() {
        if (instance == null) {
            instance = new TradeService();
        }
        return instance;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    public boolean createTrade(Trade trade) {
        return tradeRepository.save(trade);
    }

    public boolean deleteTrade(UUID tradeId, int userId) {
        return tradeRepository.delete(tradeId, userId);
    }
}

