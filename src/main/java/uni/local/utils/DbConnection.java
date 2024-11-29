package uni.local.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection
{
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

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

