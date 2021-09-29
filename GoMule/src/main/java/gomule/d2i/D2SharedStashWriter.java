package gomule.d2i;

import gomule.item.D2Item;
import gomule.util.D2BitReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class D2SharedStashWriter {
    private final File file;
    private final byte[] originalContent;

    public D2SharedStashWriter(File file, byte[] originalContent) {
        this.file = file;
        this.originalContent = originalContent;
    }

    public D2SharedStashWriter(String filename, byte[] originalContent) {
        this(new File(filename), originalContent);
    }


    public void write(D2SharedStash stash) {
        D2BitReader bitReader = new D2BitReader(originalContent.clone());
        int[] jms = bitReader.find_flags("JM");
        if (jms.length != 3) throw new RuntimeException("Stash unsupported");
        bitReader.set_byte_pos(0);
        byte[] originalHeader = bitReader.get_bytes(jms[0]);
        List<byte[]> paneBytes = stash.getPanes().stream().map(this::toBytes).collect(Collectors.toList());
        byte[] concatenate = concatenate(originalHeader, paneBytes);
        bitReader.setBytes(concatenate);
        bitReader.save(file.getAbsolutePath());
    }

    private byte[] concatenate(byte[] originalHeader, List<byte[]> panes) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(originalHeader);
            for (byte[] pane : panes) {
                outputStream.write(pane);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toBytes(D2SharedStash.D2SharedStashPane pane) {
        int itemByteLength = pane.getItems().stream().map(it -> it.get_bytes().length).reduce(0, Integer::sum);
        D2BitReader writer = new D2BitReader(new byte[4 + itemByteLength]);
        writer.write(19786, 16);
        List<D2Item> items = pane.getItems();
        writer.write(items.size(), 16);
        for (int i = 0; i < items.size(); i++) {
            byte[] bytesToWrite = items.get(i).get_bytes();
            writer.setBytes(4 + (i * bytesToWrite.length), bytesToWrite);
        }
        return writer.getFileContent();
    }
}
