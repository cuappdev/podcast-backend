package integration;

import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import podcast.services.BookmarksService;

public class BookmarksTest extends BaseIntegrationTest {

  @Autowired
  @Getter BookmarksService bookmarksService;

  @Before
  public void before() throws Exception {
    super.before();
  }

  private void bookmarkAll() throws Exception {

  }

  @Test
  public void createBookmark() throws Exception {

  }

  @After
  public void cleanup() throws Exception {
    // TODO
    super.cleanup();
  }
}
