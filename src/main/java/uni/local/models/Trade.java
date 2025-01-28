package uni.local.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private UUID id = UUID.randomUUID();
    private UUID cardToTrade;
    private String type;
    private Float minimumDamage;
    private int ownerId;
}

