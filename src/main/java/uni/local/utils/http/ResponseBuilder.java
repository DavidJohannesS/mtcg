package uni.local.utils.http;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import uni.local.utils.http.Constants;

@Getter
@Setter
@Builder

public class ResponseBuilder 
{
    private int statusCode;
    private String contentType;
    private String body;

    public String buildResponse()
    {
        return "HTTP/1.1 " + statusCode + " " + Constants.defineType ( statusCode ) + "\r\n" +
               "Content-Type: " + contentType + "\r\n" +
               "\r\n" +
               body + "\r\n" ;
    }
}
