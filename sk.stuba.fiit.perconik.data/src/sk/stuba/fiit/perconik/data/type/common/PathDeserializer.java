package sk.stuba.fiit.perconik.data.type.common;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class PathDeserializer extends StdScalarDeserializer<Path> {
  private static final long serialVersionUID = 0L;

  public PathDeserializer() {
    super(Path.class);
  }

  @Override
  public Path deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
    return Paths.get(parser.getValueAsString());
  }
}
