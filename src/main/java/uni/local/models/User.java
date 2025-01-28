package uni.local.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User
{
    private int id;
    private  String username;
    private  String password;
    private int coins;
    private int elo;
    public User ( String username, String password, int coins,int elo)
    {
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.elo = elo;
    }

}

