package cds.dsi.gestion_commerciale.app;

import cds.dsi.gestion_commerciale.dto.ExcelRowDTO;
import cds.dsi.gestion_commerciale.service.ExcelUploadServiceImpl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class ExcelUploadServiceMain {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -cp <classpath> cds.dsi.gestion_commerciale.app.ExcelUploadServiceMain <path-to-xlsx>");
            return;
        }

        String path = args[0];
        System.out.println("Lecture du fichier : " + path);

        try (InputStream is = new FileInputStream(path)) {
            // instantiate impl in "test mode" (no repos)
            ExcelUploadServiceImpl service = new ExcelUploadServiceImpl();

            // parse
            List<ExcelRowDTO> rows = service.parseExcelFile(is);
            System.out.println("Nombre de lignes parsées : " + rows.size());

            // validate syntax for each line and print summary
            int valid = 0;
            int invalid = 0;
            for (ExcelRowDTO r : rows) {
                System.out.println("=== Ligne " + r.getRowNumber() + " ===");
                System.out.println(r);
                List<String> errs = service.validateRowSyntax(r);
                if (errs.isEmpty()) {
                    System.out.println("  OK (syntaxique)");
                    valid++;
                } else {
                    System.out.println("  ERREURS : " + errs);
                    invalid++;
                }
            }

            System.out.println("Récapitulatif : valid=" + valid + " invalid=" + invalid);
        }
    }
}
