package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class IdentifierGeneratorImpl implements IdentifierGenerator {

    @Override
    public String generateUniqueIdentifier() {
        Random random = new Random();
        return IntStream.range(0, 15)
                .mapToObj(i -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
    }
}
