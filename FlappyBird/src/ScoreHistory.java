import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/** Menyimpan & membaca riwayat skor dari file history.txt (format: skor;waktu). */
public class ScoreHistory {
    private static final String FILE_NAME = "history.txt";
    private final ArrayList<String[]> entries = new ArrayList<>();

    public ScoreHistory() {
        load();
    }

    private void load() {
        entries.clear();
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";", 2);
                if (p.length == 2) entries.add(p);
            }
        } catch (IOException ignored) {}
    }

    public void add(int score) {
        String time = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        entries.add(new String[]{String.valueOf(score), time});
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            pw.println(score + ";" + time);
        } catch (IOException ignored) {}
    }

    public int getBest() {
        int best = 0;
        for (String[] e : entries) best = Math.max(best, Integer.parseInt(e[0]));
        return best;
    }

    public boolean isEmpty() { return entries.isEmpty(); }

    /** Daftar skor terbaru (paling baru di atas), maksimal 'max' baris. */
    public String toText(int max) {
        if (entries.isEmpty()) return "Belum ada riwayat skor.";
        StringBuilder sb = new StringBuilder("Skor terbaik: " + getBest() + "\n\n");
        int count = 0;
        for (int i = entries.size() - 1; i >= 0 && count < max; i--, count++) {
            sb.append(count + 1).append(". Skor ").append(entries.get(i)[0])
              .append("  (").append(entries.get(i)[1]).append(")\n");
        }
        return sb.toString();
    }

    public String toHtml(int max) {
        return "<html><div style='text-align:center'>" +
               toText(max).replace("\n", "<br>") + "</div></html>";
    }
}
