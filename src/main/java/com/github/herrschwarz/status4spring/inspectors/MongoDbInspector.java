package com.github.herrschwarz.status4spring.inspectors;

import static org.springframework.util.StringUtils.isEmpty;

import com.google.common.base.Joiner;
import com.mongodb.CommandResult;
import com.mongodb.DB;

public class MongoDbInspector implements HealthInspector {

  private static final String MONGO_STATUS = "status = ";
  private static final String MONGO_STATUS_OK = MONGO_STATUS + "OK";
  private static final String MONGO_STATUS_ERROR = MONGO_STATUS + "ERROR";
  private static final String MONGO_STATUS_WARNING = MONGO_STATUS + "WARNING";
  private String name;
  private DB mongoClientDB;

  public MongoDbInspector(String name, DB mongoClientDB) {
    this.name = name;
    this.mongoClientDB = mongoClientDB;
  }

  @Override
  public InspectionResult inspect() {
    Joiner joiner = Joiner.on(", ").skipNulls();
    boolean mongoOk = true;
    int numberOfCollections = 0;
    String message = "name = ";
    try {
      message += mongoClientDB.getName();
      numberOfCollections = mongoClientDB.getCollectionNames().size();
      CommandResult stats = mongoClientDB.getStats();
      if (!stats.ok() || !isEmpty(stats.getErrorMessage())) {
        message = joiner.join(message, MONGO_STATUS_WARNING + " (" + stats.getErrorMessage() + ")");
        mongoOk = false;
      }
      if (numberOfCollections == 0) {
        message =  joiner.join(message, MONGO_STATUS_WARNING + " (No Collections found)");
        mongoOk = false;
      }
    } catch (Exception e) {
      message = joiner.join(message, MONGO_STATUS_ERROR + " (" + e + ")");
      mongoOk = false;
    }

    if (mongoOk) {
      message = joiner.join(message, MONGO_STATUS_OK);
    }

    message = joiner.join(message, "Collections = " + numberOfCollections);
    return new InspectionResult(name, mongoOk, message);
  }

  @Override
  public String getName() {
    return name;
  }
}
