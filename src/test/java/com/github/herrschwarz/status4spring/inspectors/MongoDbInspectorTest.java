package com.github.herrschwarz.status4spring.inspectors;

import com.github.herrschwarz.status4spring.groups.UnitTest;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class MongoDbInspectorTest {

  @Mock
  private DB mongoDb;

  @Mock
  private CommandResult commandResult;

  @Test
  public void shouldDetectMissingMongoDB() {
    // Given
    when(mongoDb.getCollectionNames()).thenThrow(new MongoException("test"));
    when(mongoDb.getName()).thenReturn("test");
    MongoDbInspector inspector = new MongoDbInspector("mongoDb", this.mongoDb);

    // When
    InspectionResult result = inspector.inspect();

    // Then
    assertThat(result.isSuccessful(), is(false));
    assertThat(result.getDescription(),
        is("name = test, status = ERROR (com.mongodb.MongoException: test), Collections = 0"));
  }

  @Test
  public void shouldDetectMongoDBNotOk() {
    // Given
    HashSet<String> collections = new HashSet<>();
    collections.add("test");
    when(mongoDb.getCollectionNames()).thenReturn(collections);
    when(mongoDb.getStats()).thenReturn(commandResult);
    when(mongoDb.getName()).thenReturn("test");
    when(commandResult.ok()).thenReturn(false);
    MongoDbInspector inspector = new MongoDbInspector("mongoDb", this.mongoDb);

    // When
    InspectionResult result = inspector.inspect();

    // Then
    assertThat(result.isSuccessful(), is(false));
    assertThat(result.getDescription(),
        is("name = test, status = WARNING (null), Collections = 1"));
  }

  @Test
  public void shouldDetectMongoDBOk() throws Exception {
    HashSet<String> collections = new HashSet<>();
    collections.add("test");
    when(mongoDb.getCollectionNames()).thenReturn(collections);
    when(mongoDb.getStats()).thenReturn(commandResult);
    when(commandResult.ok()).thenReturn(true);
    when(mongoDb.getName()).thenReturn("test");
    MongoDbInspector inspector = new MongoDbInspector("mongoDb", this.mongoDb);
    InspectionResult result = inspector.inspect();
    assertThat(result.isSuccessful(), is(true));
    assertThat(result.getDescription(), is("name = test, status = OK, Collections = 1"));
  }

  @Test
  public void shouldDetectEmptyDb() {
    when(mongoDb.getCollectionNames()).thenReturn(new HashSet<>());
    when(mongoDb.getStats()).thenReturn(commandResult);
    when(commandResult.ok()).thenReturn(true);
    when(mongoDb.getName()).thenReturn("test");
    MongoDbInspector inspector = new MongoDbInspector("mongoDb", this.mongoDb);
    InspectionResult result = inspector.inspect();
    assertThat(result.isSuccessful(), is(false));
    assertThat(result.getDescription(),
        is("name = test, status = WARNING (No Collections found), Collections = 0"));

  }
}