package uni.local.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection
{
   /* private static final String URL = "jdbc:postgresql://localhost:5432/mtcg";
    private static final String USER = "postgres";
    private static final String PASSWORD = "testpwd";
    */
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static Connection connection;

    private DbConnection () {}

    public static synchronized Connection getInstance () throws SQLException
    {
        if ( connection == null || connection.isClosed() )
        {
            connection = DriverManager.getConnection( URL, USER, PASSWORD );
        }
        return connection;
    }
}

