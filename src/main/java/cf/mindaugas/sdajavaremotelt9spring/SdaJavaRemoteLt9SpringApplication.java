package cf.mindaugas.sdajavaremotelt9spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SdaJavaRemoteLt9SpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(SdaJavaRemoteLt9SpringApplication.class, args);
    }
}

@RestController
class BlogPostController {
    // ... fake database
    List<BlogPost> blogposts = new ArrayList<>(){{
        add(new BlogPost(1, "Weather is nice", "Sunny and awesome"));
        add(new BlogPost(2, "Crypto is down", "Markets are red"));
    }};
    // List<BlogPost> blogposts = new ArrayList<>();

    // curl http://localhost/api/blogposts -s
    @GetMapping("/api/blogposts")
    public List<BlogPost> getAllBlogPosts() {
        return this.blogposts;
    }

    @PostMapping("/api/blogposts")
    public void createBlogPost(@RequestBody BlogPost blogPost) {
        this.blogposts.add(blogPost);
    }

    @DeleteMapping("/api/blogposts/{id}")
    public void deleteBlogPost(@PathVariable int id) {
        // System.out.println(id);
        // blogposts.
        // TODO
    }

    // What is the difference btw/ @RequestBody, @RequestParam, @PathVariable
    // ... curl -X POST 'http://localhost/api/test/Mindaugas?test2=Jonaitis' --header 'Content-Type: application/json' --data-raw 'Kazys' -s
    @PostMapping("/api/test/{test1}")
    public String test(
        @PathVariable String test1,
        @RequestParam String test2,
        @RequestParam(required = false) String test3,
        @RequestBody String test4
    ) {
        return "Pathvar: " + test1
            + ", req param 1: " + test2
            + ", req param 2: " + test3
            + ", req body param: " + test4
            ;
    }
}

@Data
@AllArgsConstructor
class BlogPost {
    private int id;
    private String title;
    private String text;
}


@Component("myHealthCheck")
class HealthCheck implements HealthIndicator {
    @Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down().withDetail("Message", "Database down").build();
        }
        return Health.up().build();
    }

    public int check() {
        // Our logic to check health
        // ... example: send a simple select statement to the database
        return 1;
    }
}