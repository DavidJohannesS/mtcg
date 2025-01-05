package uni.local.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Package {
    private int id;
    private List<Card> cards;

    public Package(List<Card> cards) {
        this.cards = cards;
    }
}

