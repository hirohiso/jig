package jig.domain.model.characteristic;

import jig.domain.model.identifier.type.TypeIdentifier;
import jig.domain.model.identifier.type.TypeIdentifiers;

public interface CharacteristicRepository {

    TypeIdentifiers getTypeIdentifiersOf(Characteristic characteristic);

    void register(TypeCharacteristics typeCharacteristics);

    Characteristics findCharacteristics(TypeIdentifier typeIdentifier);

    Characteristics findCharacteristics(TypeIdentifiers typeIdentifiers);
}
