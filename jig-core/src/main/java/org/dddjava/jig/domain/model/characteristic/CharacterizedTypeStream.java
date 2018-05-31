package org.dddjava.jig.domain.model.characteristic;

import org.dddjava.jig.domain.model.identifier.type.TypeIdentifier;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifiers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 特徴付けられた型一覧のストリーム
 */
public class CharacterizedTypeStream {

    Stream<CharacterizedType> stream;

    public CharacterizedTypeStream(Stream<CharacterizedType> stream) {
        this.stream = stream;
    }

    public Characteristics characteristics() {
        return stream.map(CharacterizedType::characteristics).collect(Characteristics.collector());
    }

    public CharacterizedTypeStream filter(TypeIdentifiers typeIdentifiers) {
        Set<TypeIdentifier> set = typeIdentifiers.set();
        return new CharacterizedTypeStream(stream.filter(characterizedType -> set.contains(characterizedType.typeIdentifier())));
    }

    public TypeIdentifiers typeIdentifiers() {
        return stream.map(CharacterizedType::typeIdentifier).collect(TypeIdentifiers.collector());
    }

    public CharacterizedTypeStream filter(Characteristic characteristic) {
        return new CharacterizedTypeStream(stream.filter(typeCharacteristics -> typeCharacteristics.has(characteristic).isSatisfy()));
    }

    public CharacterizedType pickup(TypeIdentifier typeIdentifier) {
        return stream.filter(typeCharacteristics -> typeCharacteristics.typeIdentifier().equals(typeIdentifier))
                .findFirst()
                .orElseGet(() -> new CharacterizedType(typeIdentifier, Collections.emptySet()));
    }
}
