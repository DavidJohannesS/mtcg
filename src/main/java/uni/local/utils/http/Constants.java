package uni.local.utils.http;

public class Constants
{
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400; 
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_NOT_IMPLEMENTED = 418;

    public static String defineType( int statusCode )
    {
        switch ( statusCode )
        {
            case STATUS_OK: return "OK";
            case STATUS_CREATED: return "Created";
            case STATUS_BAD_REQUEST: return "Bad Request";
            case STATUS_UNAUTHORIZED: return "Unautorized";
            case STATUS_NOT_IMPLEMENTED: return "I'm a teapot";
            default: return "";
        }
    }
}
