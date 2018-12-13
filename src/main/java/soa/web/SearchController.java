package soa.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import twitter4j.Status;
import java.util.*;

@Controller
public class SearchController {

  private final ProducerTemplate producerTemplate;

  @Autowired
  public SearchController(ProducerTemplate producerTemplate) {
    this.producerTemplate = producerTemplate;
  }

  @RequestMapping("/")
  public String index() {
    return "index";
  }


  @RequestMapping(value = "/search")
  @ResponseBody
  public Object search(@RequestParam("q") String q,
                       @RequestParam("max") int max,
                       @RequestParam("numLikes") int likes,
                       @RequestParam("numRetweets") int retweets,
                       @RequestParam("date") String fecha) {
    Map<String, Object> headers = new HashMap<>();
    ArrayList<Status> newJson = new ArrayList<Status>();
    Calendar calendar = Calendar.getInstance();
    int tweetDateI, userDateI;
    String[] userDateS = fecha.split("-");

    headers.put("CamelTwitterKeywords", q);
    headers.put("CamelTwitterCount", max);

    userDateI =   Integer.valueOf(
                    String.valueOf(userDateS[2]) +
                    String.valueOf(userDateS[1]) +
                    String.valueOf(userDateS[0]));

    ArrayList<Status> json = (ArrayList<Status>) producerTemplate.requestBodyAndHeaders("direct:search", "", headers);

      for (Status aJson : json) {
          calendar.setTime(aJson.getCreatedAt());

          tweetDateI =   Integer.valueOf(
                        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                        String.valueOf(calendar.get(Calendar.MONTH)) +
                        String.valueOf(calendar.get(Calendar.YEAR)));

          if (aJson.getFavoriteCount() >= likes &&
              aJson.getRetweetCount() >= retweets &&
              userDateI <= tweetDateI)
          {
            newJson.add(aJson);
          }
      }
    return newJson;
  }
}