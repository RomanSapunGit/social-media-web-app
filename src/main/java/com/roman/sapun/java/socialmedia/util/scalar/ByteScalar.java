package com.roman.sapun.java.socialmedia.util.scalar;

import com.roman.sapun.java.socialmedia.util.ImageUtil;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Locale;

@Component
public class ByteScalar
{
    private final ImageUtil imageUtil;
    public ByteScalar(ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
    }
    public final GraphQLScalarType BYTE = GraphQLScalarType.newScalar()
            .name("Byte")
            .description("A custom scalar that handles bytes")
            .coercing(new Coercing<>() {
                @Override
                public Object serialize(Object dataFetcherResult, GraphQLContext graphQLContext, Locale locale) {
                    if (dataFetcherResult instanceof byte[]) {
                        byte[] decompressedImage = imageUtil.decompressImage((byte[]) dataFetcherResult);
                        return Base64.getEncoder().encodeToString(decompressedImage);
                    } else {
                        throw new CoercingSerializeException("Expected a byte[] object but got " + dataFetcherResult.getClass().getName());
                    }
                }

                @Override
                public Object parseValue(Object input, GraphQLContext graphQLContext, Locale locale) {
                    if (input instanceof String) {
                        byte[] compressedImageData = Base64.getDecoder().decode((String) input);
                        return imageUtil.decompressImage(compressedImageData);
                    } else {
                        throw new CoercingParseValueException("Expected a String object but got " + input.getClass().getName());
                    }
                }

                @Override
                public Object parseLiteral(Value input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) {
                    if (input instanceof StringValue) {
                        String base64String = ((StringValue) input).getValue();
                        byte[] compressedImageData = Base64.getDecoder().decode(base64String);
                        return imageUtil.decompressImage(compressedImageData);
                    } else {
                        throw new CoercingParseLiteralException("Expected a StringValue object but got " + input.getClass().getName());
                    }
                }
            }).build();

}
