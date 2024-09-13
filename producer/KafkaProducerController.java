@RestController
@RequestMapping("/kafka")
public class KafkaProducerController {
    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message) {
	kafkaProducer.sendMessage("my-topic", message);
	return ResponseEntity.ok("Message sent successfully");
    }
}

