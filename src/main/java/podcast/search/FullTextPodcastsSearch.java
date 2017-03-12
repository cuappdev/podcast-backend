package podcast.search;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Podcasts search via FTS **/
@Component
@Qualifier("fullTextPodcastsSearch")
public class FullTextPodcastsSearch {
}
