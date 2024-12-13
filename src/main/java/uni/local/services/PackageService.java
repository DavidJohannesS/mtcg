package uni.local.services;

import uni.local.models.Card;
import uni.local.models.Package;
import uni.local.repository.PackageRepository;

import java.sql.SQLException;
import java.util.List;

public class PackageService {
    private final PackageRepository packageRepository;

    public PackageService() {
        this.packageRepository = new PackageRepository();
    }

    public boolean createPackage(List<Card> cards) throws SQLException {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }
        Package cardPackage = new Package(cards);
        return packageRepository.createPackage(cardPackage);
    }
//    public boolean sellPackage(List<Card> cards, User user) throws SQLException
//    {
//        for (Card card: cards)
//        {
//            card.setPackage_id(0);
//            card.setOwner_id(user.getId());
//            cardRepository.save(card);
//        }
//        return true;
//    }

}

