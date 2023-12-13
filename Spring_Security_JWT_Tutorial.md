# Spring Security JWT Tutorial

## Importance of using a secure REST API

Using a secure REST API is one of the most important aspect of an application. Secure APIs contribute to the overall integrity of an application and without proper security measures, the API could be vulnerable to attacks like injection or man-in-the-middle, potentially leading to data manipulation or theft.

Authentication and authorization mechanisms in a secure API ensure that only authorized users or systems can access specific resources. This prevents malicious actors from exploiting vulnerabilities and gaining unauthorized control.

In the interconnected world, a breach in one system can have a cascading effect on others. Therefore, securing the API isn't just about protecting one's own data; it's also about being a responsible player in the larger digital ecosystem.

## JSON Web Tokens (JWT)

JSON Web Tokens (JWT) is a compact, URL-safe means of representing claims to be transferred between two parties. These claims are often used for authentication and authorization purposes.

JWTs consist of three parts:

- **Header** - contains the type of the token and the signing algorithm being used
```json
{ "alg": "HS256", "typ": "JWT" }
```
- **Payload** - contains the claims or the JSON object.
```json
{ "sub": "verstappen", "iat": 1701709505, "exp": 1701713105, "admin": true }
```
- **Signature** - a string that is generated via a cryptographic algorithm using a secret key that can be used to verify the integrity of the JSON payload
```
HMACSHA256(
base64UrlEncode(header) + "." + base64UrlEncode(payload), base64UrlEncode(thisismysuperduperhypersecretkey)
)
```
Combining the previous examples, the JWT will look like this:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ2ZXJzdGFwcGVuIiwiaWF0IjoxNzAxNzA5NTA1LCJleHAiOjE3MDE3MTMxMDUsImFkbWluIjp0cnVlfQ.7q9Zz7B2gzazq5vyNzbGw9X-ecCBjntXOrp0HPXV1ps
```
JWTs are commonly used for authentication. When a user logs in, the server creates a JWT and sends it back to the client. The client can then include this token in the headers of subsequent requests to authenticate themselves. This eliminates the need for the server to store session information, making JWTs stateless.

One problem is that the creation of a JWT depends on one secret key. If that key is compromised, the attacker can fabricate their own JWT which the API layer will accept spoofing any user’s identity. Changing the secret key from time to time reduces this risk.

## Spring Security

Spring Security is the primary choice for implementing application-level security in Spring applications. Generally, its purpose is to offer a highly customizable way of implementing authentication, authorization, and protection against common attacks (such as CSRF or XSS).

One of its main highlights is the security filter chain. Spring Security maintains a filter chain internally where each of the filters has a particular responsibility and filters are added or removed from the configuration depending on which services are required. What is interesting is that the filter chain intercepts the web requests before they actually arrive to the web server, leading to a much better performance.

## Implementation example of JWT authentication in Spring Security

### Configuring the filter chain

```java
// SecurityConfig class

// injected
private final HeaderAuthFilter jwtFilter;

@Bean  
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {  
    return http  
            .cors(Customizer.withDefaults())  
            .csrf(ServerHttpSecurity.CsrfSpec::disable)  
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)  
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)  
            .authorizeExchange(auth -> {  
                auth.pathMatchers("/login", "/register").permitAll();  
                auth.anyExchange().authenticated();  
            })  
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)  
            .build();  
}
```

- Using `authorizeExchange` we can decide which endpoints need authentication or not; for example, when a user wants to login or register, we don't need any authentication
- `addFilterAt` helps us declare custom filters for a particular responsibility; in our case `jwtFilter` will validate the user's JWT found in the header of a web request
### Inside the JWT filter

```java
// HeaderAuthFilter class

// injected
private final JwtService jwtService;

@Override  
public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {  
    HttpServletRequest httpRequest = (HttpServletRequest) request;  
    HttpServletResponse httpResponse = (HttpServletResponse) response;  
  
    String jwtBearer = httpRequest.getHeader("Authorization");  
  
    if (jwtBearer == null) {  
        logger.error("Invalid user token header");  
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
        return;  
    }  
  
    if (!jwtService.isJwtBearerValid(jwtBearer, null)) {  
        logger.error("Invalid user token value");  
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
        return;  
    }  
  
    filterChain.doFilter(request, response);  
}
```

- This JWT is conventionally placed in the header of a web request as the value of `Authorization` key
- The value also has a prefix by convention: `Bearer eyJhbGciOi...`
- Firstly, we want to make sure that the `Authorization` key exists and extract its value
- Secondly, we pass the value to a method (`isJwtBearerValid`) in a JWT manipulation service (`jwtService`)  that validates the token
- Finally, if the filter approves the JWT, it will call the next filter from the chain (if it exists)
- If the filter disapproves the JWT, we want to send a web response back to the client with an unauthorized status

### JWT Service

#### Verifying a JWT

```java
public boolean isJwtBearerValid(String jwtBearer) {
	Claims claims;
	try {
		// eliminate "Bearer " from string first  
	    claims = extractClaims(jwtBearer.substring(7));  
	} catch (Exception e) {  
	    logger.error("Invalid JWT value");  
	    return false;  
	}
	
    return !claims.getExpiration().before(new Date(System.currentTimeMillis()));
}
```
- We want to validate two main things
    -  Make sure the we can extract the claims from the JWT successfully
    -  Make sure the JWT is not expired - JWT uses some special keys that follow an industry convention for the names of important fields. For example, `exp` is the time (in milliseconds since epoch) the JWT will expire.
```java
// should be hidden very well
private String JWT_SECRET;

private Claims extractClaims(String jwt) {  
    return Jwts.parserBuilder()  
            .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))  
            .build()  
            .parseClaimsJws(jwt)  
            .getBody();  
}
```
- Extracting the claims from the JWT successfully means
    - The JWT follows the `header.payload.signature` pattern
    - By decoding the JWT, we can recreate the `header` and `payload` JSON objects
    - The signature is valid (meaning that the data inside the JWT has not been tempered with) when we apply the secret key `JWT_SECRET` hidden in the issuer (in our case, the web server)
- If there are any errors, an exception will be thrown and caught by `isJwtBearerValid`, which will then return `false`.

#### Generating a JWT

```java
// in ms, for example 3600000 (1000 * 60 * 60) means that the JWT expires in 1 hour
private long JWT_EXPIRATION;

public String generateJwt(String username) {  
    Date issueDate = new Date(System.currentTimeMillis());  
    Date expireDate = new Date(issueDate.getTime() + JWT_EXPIRATION);  
  
    JwtBuilder jwtBuilder = Jwts.builder()  
            .setSubject(username)  
            .setIssuedAt(issueDate)  
            .setExpiration(expireDate)  
            .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()));  
  
    return jwtBuilder.compact();  
}
```
- When we generate a JWT, we can choose what attributes to add in the payload. By using a library, we can easily add different conventional claims
    - `setSubject` sets the key `sub` - in our case, we want to set the value to the username who wants to log in
    - `setIssuedAt` sets the key `iat` - this the time (in milliseconds since epoch) the JWT was created
    - `setExpiration` set the key `exp` - explained in the previous sections, it used to check if the JWT has expired
- `signWith` signs the key with the secret key `JWT_SECRET`

### Strengths and possible further enhancements

This is a simple and basic method to implement authentication on a web server using Spring Security. Being stateless, verifying a JWT doesn't require any form of storage, making a good option for a web server with many registered users. Instead, the client has to store the JWT (usually in a cookie) and submit it every time he wants to make a web request.

Unfortunately, its stateless status also comes with a disadvantage. If we want to implement a more complex system where users can be banned from the web server, it is not that easy to revoke a JWT before it expires naturally. In this case, a web server might need a way to maintain a JWT black list, either in memory or even a database. 