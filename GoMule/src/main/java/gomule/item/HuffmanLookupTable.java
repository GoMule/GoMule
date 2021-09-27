package gomule.item;

import gomule.util.D2BitReader;

import java.util.HashMap;
import java.util.Map;

public class HuffmanLookupTable {

    private Map<String, Character> dictionary;

    public HuffmanLookupTable(Map<String, Character> dictionary) {
        this.dictionary = dictionary;
    }

    public Character lookupBinarySequence(String bitString) {
        return dictionary.get(bitString);
    }

    public String readHuffmanEncodedString(D2BitReader pFile) {
        StringBuilder reader = new StringBuilder();
        StringBuilder result = new StringBuilder();
        Character character;
        do {
            reader.append(pFile.read(1));
            character = lookupBinarySequence(reader.toString());
            if (character != null && ' ' != character) {
                result.append(character);
                reader.setLength(0);
            }
            if (reader.length() > 100 || result.length() > 100)
                throw new RuntimeException("Huffman decoding failed, string too long");
        } while (character == null || ' ' != character);
        return result.toString();
    }

    public static HuffmanLookupTable withStandardDictionary() {
        Map<String, Character> dictionary = new HashMap<>();
        dictionary.put("11110", 'a');
        dictionary.put("010011", 'f');
        dictionary.put("010010", 'k');
        dictionary.put("11001", 'p');
        dictionary.put("00001", 'u');
        dictionary.put("11011000", 'z');
        dictionary.put("11111010", '4');
        dictionary.put("01110", '9');
        dictionary.put("0101", 'b');
        dictionary.put("11010", 'g');
        dictionary.put("11101", 'l');
        dictionary.put("11011001", 'q');
        dictionary.put("1101110", 'v');
        dictionary.put("11111011", '0');
        dictionary.put("00010110", '5');
        dictionary.put("10", ' ');
        dictionary.put("01000", 'c');
        dictionary.put("00011", 'h');
        dictionary.put("01101", 'm');
        dictionary.put("11100", 'r');
        dictionary.put("00000", 'w');
        dictionary.put("1111100", '1');
        dictionary.put("1101111", '6');
        dictionary.put("110001", 'd');
        dictionary.put("1111110", 'i');
        dictionary.put("001101", 'n');
        dictionary.put("0010", 's');
        dictionary.put("00111", 'x');
        dictionary.put("001100", '2');
        dictionary.put("01111", '7');
        dictionary.put("110000", 'e');
        dictionary.put("000101110", 'j');
        dictionary.put("1111111", 'o');
        dictionary.put("01100", 't');
        dictionary.put("0001010", 'y');
        dictionary.put("1101101", '3');
        dictionary.put("000100", '8');
        return new HuffmanLookupTable(dictionary);
    }
}


