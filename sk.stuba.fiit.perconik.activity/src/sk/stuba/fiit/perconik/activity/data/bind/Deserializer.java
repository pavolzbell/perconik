package sk.stuba.fiit.perconik.activity.data.bind;

import java.io.IOException;
import java.util.Map;
import sk.stuba.fiit.perconik.activity.data.AnyData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

public final class Deserializer extends UntypedObjectDeserializer
{
	private static final long serialVersionUID = 0L;

	public Deserializer()
	{
	}

	@Override
	protected final Object mapObject(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException
	{
		return new AnyData((Map<String, Object>) super.mapObject(parser, context));
	}
}
