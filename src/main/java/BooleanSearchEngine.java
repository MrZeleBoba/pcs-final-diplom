import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> wordList = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] files = pdfsDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().contains(".pdf")) {
                    PdfDocument document = new PdfDocument(new PdfReader(file));
                    for (int i = 0; i < document.getNumberOfPages(); i++) {
                        PdfPage page = document.getPage(i + 1);
                        String text = PdfTextExtractor.getTextFromPage(page);// получить текст со страницы
                        String[] words = text.split("\\P{IsAlphabetic}+");// разбить текст на слова
                        Map<String, Integer> freqs = new HashMap<>();
                        for (var word : words) {
                            if (word.isEmpty()) {
                                continue;
                            }
                            int f = freqs.getOrDefault(word, 0) + 1;
                            freqs.put(word.toLowerCase(), f);
                        }
                        for (String word : freqs.keySet()) {
                            PageEntry pageEntry = new PageEntry(file.getName(), i, freqs.get(word));
                            if (wordList.containsKey(word)) {
                                wordList.get(word).add(pageEntry);
                            } else {
                                wordList.put(word, new ArrayList<>());
                                wordList.get(word).add(pageEntry);
                            }
                        }
                    }
                    document.close();
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> list = wordList.get(word);
        if (word.equals(wordList)) {
            list.add((PageEntry) wordList.entrySet());
        }
        Collections.sort(list);
        return list;
    }

}
