package cf.mindaugas.sdajavaremotelt9spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class SdaJavaRemoteLt9SpringApplication {
    public static void main(String[] args) {
        log.info("Starting app");
        SpringApplication.run(SdaJavaRemoteLt9SpringApplication.class, args);
        log.error("App started");
    }
}

// @RestController
// @Slf4j
// class BlogPostController {
//     // ... fake database
//     List<BlogPost> blogposts = new ArrayList<>() {{
//         add(new BlogPost(1L, "Weather is nice", "Sunny and awesome"));
//         add(new BlogPost(2L, "Crypto is down", "Markets are red"));
//     }};
//
//     // curl http://localhost/api/blogposts -s
//     @GetMapping("/api/blogposts")
//     public List<BlogPost> getAllBlogPosts() {
//         return this.blogposts;
//     }
//
//     @GetMapping("/api/blogposts/{id}")
//     public ResponseEntity<Object> getBlogPost(@PathVariable int id) {
//         var bp = blogposts.stream()
//                 .filter(blogPost -> blogPost.getId() == id)
//                 .findFirst();
//         return bp.isPresent()
//                 ? new ResponseEntity<>(bp.get(), HttpStatus.OK)
//                 : new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
//     }
//
//     // curl -X POST 'http://localhost/api/blogposts' -H 'Content-Type: application/json' -d '{ "id": 88, "title": "Weather is amazing", "text": "Sunny and hot!" }'
//     // ... test this curl with gitbash
//     @PostMapping("/api/blogposts")
//     public void createBlogPost(@RequestBody BlogPost blogPost) {
//         this.blogposts.add(blogPost);
//     }
//
//     @PutMapping("/api/blogposts/{id}")
//     public ResponseEntity<Void> createBlogPost(@PathVariable int id, @RequestBody BlogPost blogPost) {
//         var bpo = blogposts.stream()
//                 .filter(post -> post.getId() == id)
//                 .findFirst();
//         if(bpo.isPresent()){
//             var bp= bpo.get();
//             bp.setId(blogPost.getId());
//             bp.setText(blogPost.getText());
//             bp.setTitle(blogPost.getTitle());
//             return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//         }
//         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//     }
//
//     @DeleteMapping("/api/blogposts/{id}")
//     public ResponseEntity<Object> deleteBlogPost(@PathVariable int id) {
//         log.error(String.valueOf(id));
//         // blogposts.removeIf(blogPost -> blogPost.getId() == id);
//
//         // 0. What if we want to return 204 on successful delete and
//         // ... 404 if the item was not found?
//         // ResponseEntity<Void>
//         // return blogposts.removeIf(blogPost -> blogPost.getId() == id)
//         //         ? new ResponseEntity<>(HttpStatus.NO_CONTENT)  // 204
//         //         : new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
//
//         // 1. What if we want to return a response body
//         // ResponseEntity<String>
//         // return blogposts.removeIf(blogPost -> blogPost.getId() == id)
//         //         ? new ResponseEntity<>("OK", HttpStatus.NO_CONTENT)  // 204 (does not return content!)
//         //         : new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND); // 404
//
//         // 2. What if we want to return what we deleted
//         var deletable = blogposts.stream()
//                 .filter(blogPost -> blogPost.getId() == id)
//                 .findFirst();
//         if(deletable.isPresent()) {
//             blogposts.remove(deletable.get());
//             return new ResponseEntity<>(deletable, HttpStatus.OK); // 204 (does not return content!)
//         }
//         return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND); // 404
//     }
//
//     // What is the difference btw/ @RequestBody, @RequestParam, @PathVariable
//     // ... curl -X POST 'http://localhost/api/test/Mindaugas?test2=Jonaitis' --header 'Content-Type: application/json' --data-raw 'Kazys' -s
//     @PostMapping("/api/test/{test1}")
//     public String test(
//             @PathVariable String test1,
//             @RequestParam String test2,
//             @RequestParam(required = false) String test3,
//             @RequestBody String test4
//     ) {
//         return "Pathvar: " + test1
//                 + ", req param 1: " + test2
//                 + ", req param 2: " + test3
//                 + ", req body param: " + test4
//                 ;
//     }
// }

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
class BlogPostControllerWRepo {
    @Autowired
    private BlogPostRepository bpr;

    @GetMapping("/blogposts")
    public Iterable<BlogPost> getAllBlogPosts() {
        return bpr.findAll();
    }

    @GetMapping("/blogposts/{id}")
    public ResponseEntity<Object> getBlogPost(@PathVariable Long id) {
        Optional<BlogPost> obp = bpr.findById(id);
        return obp.isPresent()
                ? new ResponseEntity<>(obp.get(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/blogposts/{id}/enhanced")
    public BlogPost getBlogPostEnhanced(@PathVariable Long id) {
        return bpr.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/blogposts")
    public ResponseEntity<Void> createBlogPost(@RequestBody BlogPost blogPost) {
        this.bpr.save(blogPost);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/blogposts/{id}")
    public ResponseEntity<Void> createBlogPost(@PathVariable Long id, @RequestBody BlogPost blogPost) {
        // load
        BlogPost bpToUpdate = bpr.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // set
        bpToUpdate.setTitle(blogPost.getTitle());
        bpToUpdate.setText(blogPost.getText());

        // save
        bpr.save(bpToUpdate);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/blogposts/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
        try {
            this.bpr.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
    }

    @GetMapping("/blogposts/find_by")
    public ResponseEntity<List<BlogPost>> getByTitle(@RequestParam String title) {
        Optional<List<BlogPost>> obp = bpr.findByTitle(title);
        return obp.isPresent()
                ? new ResponseEntity<>(obp.get(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

@RestController
class ErrorHandlerController implements ErrorController {
    @RequestMapping("/error")
    @ResponseBody
    public String getErrorPath() {
        return "";
    }
}

@Repository
interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<List<BlogPost>> findByTitle(String title);
    Optional<List<BlogPost>> findByTitleIgnoreCase(String title);
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
class BlogPost {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String text;
}

@Service
class DbInit implements CommandLineRunner {

    @Autowired
    private BlogPostRepository bpr;

    @Override
    public void run(String... args) {
        // Delete all in the beginning
        this.bpr.deleteAll();

        // Create initial dummy products
        BlogPost bp1 = new BlogPost(1L, "Snowboard", "A");
        BlogPost bp2 = new BlogPost(2L, "Kittens", "B");
        BlogPost bp3 = new BlogPost(3L, "Small dogs", "C");
        BlogPost bp4 = new BlogPost(4L, "Tesla P100", "D");
        BlogPost bp5 = new BlogPost(5L, "kittens", "E");

        // Save to db
        this.bpr.saveAll(Arrays.asList(bp1,bp2,bp3,bp4, bp5));
    }
}


@Component
class StartJobsManager implements CommandLineRunner {
    static Logger LOG = LoggerFactory.getLogger(StartJobsManager.class);

    @Override
    public void run(String... args) {
        LOG.info(">>>> EXECUTING : command line runner");
    }
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