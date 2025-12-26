// import io.jsonwebtoken.security.Keys;
// import javax.crypto.SecretKey;
// import java.util.Base64;

// public class KeyGenerator {
//     public static void main(String[] args) {
//         SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
//         String secretString = Base64.getEncoder().encodeToString(key.getEncoded());
//         System.out.println("Generated JWT Secret: " + secretString);
//     }
// }


// command for generating jwt secret
// $bytes = New-Object byte[] 64
// [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
// [Convert]::ToBase64String($bytes)
