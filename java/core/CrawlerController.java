package core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping("/run-crawler")
    public ResponseEntity<List<Map<String, Object>>> runCrawler() {
        try {
            List<Map<String, Object>> results = crawlerService.run();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
