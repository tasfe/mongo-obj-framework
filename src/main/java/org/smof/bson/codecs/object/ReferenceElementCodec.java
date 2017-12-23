package org.smof.bson.codecs.object;

import static org.smof.utils.BsonUtils.*;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.smof.annnotations.SmofObject;
import org.smof.bson.codecs.SmofCollectibleCodec;
import org.smof.bson.codecs.SmofEncoderContext;
import org.smof.collection.SmofDispatcher;
import org.smof.collection.SmofOpOptions;
import org.smof.collection.SmofOpOptionsImpl;
import org.smof.element.Element;
import org.smof.field.MasterField;
import org.smof.field.PrimaryField;
import org.smof.field.SecondaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

class ReferenceElementCodec<T extends Element> implements SmofCollectibleCodec<T> {
	
	private final SmofDispatcher dispatcher;
	private final CodecRegistry registry;
	private final ParserCache parserCache;
	private final Codec<T> objectCodec;
	private final LazyLoader lazyLoader;
	private final Class<T> type;
	
	ReferenceElementCodec(Class<T> type, SmofParser topParser, LazyLoader lazyLoader, Codec<T> objectCodec) {
		this.lazyLoader = lazyLoader;
		this.type = type;
		this.dispatcher = topParser.getDispatcher();
		this.registry = topParser.getRegistry();
		this.parserCache = topParser.getCache();
		this.objectCodec = objectCodec;
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		final ObjectId id = getDocumentId(value).asObjectId().getValue();
		getCodecOrThrow(ObjectId.class, registry).encode(writer, id, encoderContext);
	}

	@Override
	public void encode(BsonWriter writer, T value, SmofEncoderContext context) {
		final SmofField field = context.getField();
		if(field instanceof MasterField) {
			encodeAsMasterField(writer, value);
		}
		else if(field instanceof PrimaryField) {
			encodeAsPrimaryField(writer, value, context, (PrimaryField) field);
		}
		else {
			encode(writer, value, EncoderContext.builder().build());
		}
	}

	private void encodeAsPrimaryField(BsonWriter writer, T value, SmofEncoderContext context, PrimaryField field) {
		final SmofObject annotation = field.getSmofAnnotationAs(SmofObject.class);
		if(annotation.preInsert()) {
			writer.writeObjectId(insert(value));
		}
		else {
			context.addPosInsertionHook(update -> {
				update.set(field.getName(), new BsonObjectId(insert(value)));
			});
		}
	}

	private void encodeAsMasterField(BsonWriter writer, T value) {
		final EncoderContext encoderContext = EncoderContext.builder()
				.isEncodingCollectibleDocument(true)
				.build();
		parserCache.put(value, getDocumentId(value));
		objectCodec.encode(writer, value, encoderContext);
		parserCache.remove(value);
	}
	
	private ObjectId insert(T element) {
		final SmofOpOptions options = new SmofOpOptionsImpl();
		options.bypassCache(true);
		dispatcher.insert(element, options);
		final BsonObjectId id = getDocumentId(element).asObjectId();
		parserCache.put(element, id);
		return id.getValue();
	}

	@Override
	public Class<T> getEncoderClass() {
		return type;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		return fetch(reader);
	}

	@Override
	public T decode(BsonReader reader, SmofEncoderContext context) {
		final SmofField field = context.getField();
		if(field instanceof MasterField) {
			return objectCodec.decode(reader, DecoderContext.builder().build());
		}
		else if(field instanceof SecondaryField) {
			return fetch(reader);
		}
		else if(field instanceof PrimaryField) {
			return lazyLoad(reader);
		}
		throw new UnsupportedOperationException("Cannot handle field: " + field);
	}

	private T lazyLoad(BsonReader reader) {
		final ObjectId id = getCodecOrThrow(ObjectId.class, registry)
				.decode(reader, DecoderContext.builder().build());
		return lazyLoader.createLazyInstance(type, id);
	}

	private T fetch(BsonReader reader) {
		return dispatcher.findById(reader.readObjectId(), getEncoderClass());
	}

	@Override
	public T generateIdIfAbsentFromDocument(T document) {
		if(!documentHasId(document)) {
			document.setId(new ObjectId());
		}
		return document;
	}

	@Override
	public boolean documentHasId(T document) {
		return document.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(T document) {
		final T documentWithId = generateIdIfAbsentFromDocument(document);
		return new BsonObjectId(documentWithId.getId());
	}

}