package uni.local.models;

import lombok.AllArgsConstructor;
import lombok.Data;
//import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class User
{
    private final String username;
    private final String password;
    private int coins;
}

