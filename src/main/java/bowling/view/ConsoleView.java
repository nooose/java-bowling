package bowling.view;

import bowling.domain.FrameInfo;
import bowling.domain.ScoreSheetReader;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsoleView {

    private PrintWriter writer = new PrintWriter(System.out);

    public void printScoreSheets(List<ScoreSheetReader> readers) {

        List<String> playerNames = readers.stream()
                .map(ScoreSheetReader::readPlayName)
                .collect(Collectors.toList());
        int maxNameLength = playerNames.stream()
                .map(String::length)
                .reduce(Math::max)
                .orElse(8);
        int nameSpan = Math.max(maxNameLength + 2, 8);

        writer.print(String.format("| %1$" + nameSpan + "s | ", "NAME"));
        String frameNos = IntStream.range(0, 10)
                .mapToObj(idx -> {
                    if (idx == 9) {
                        return String.format("%1$4s ", (idx + 1));
                    }
                    return String.format("%1$2s ", (idx + 1));
                })
                .collect(Collectors.joining(" | "));
        writer.print(frameNos);
        writer.println(" |");

        readers.forEach(reader -> {
            writer.print(String.format("|%1$" + nameSpan + "s  | ", reader.readPlayName()));
            while (!reader.isEOF()) {
                FrameInfo frameInfo = reader.readFrameInfo();
                String marked = frameInfo.getPinMarkSigns()
                        .stream()
                        .collect(Collectors.joining("|"));
                int spanSize = 3;
                if (frameInfo.getFrameNo() == 10) {
                    spanSize = spanSize + 2;
                }
                writer.print(String.format("%1$" + spanSize + "s", marked));
                writer.print(" | ");
            }
            writer.println();
        });
        writer.flush();
    }
}