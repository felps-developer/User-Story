package com.supera.accessrequest.util;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gerador de PDF do relatório JaCoCo.
 * 
 * Converte o relatório HTML do JaCoCo em um arquivo PDF.
 * 
 * Uso:
 * - Execute: mvn clean test jacoco:report exec:java@generate-pdf
 * - Ou use o perfil: mvn clean test jacoco:report -Ppdf
 */
public class JacocoPdfGenerator {

    private static final String HTML_REPORT_PATH = "target/site/jacoco/index.html";
    private static final String PDF_OUTPUT_PATH = "target/site/jacoco/relatorio-jacoco.pdf";

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("Gerador de PDF do Relatório JaCoCo");
        System.out.println("==========================================");
        System.out.println();

        try {
            // 1. Verificar se o arquivo HTML existe
            File htmlFile = new File(HTML_REPORT_PATH);
            if (!htmlFile.exists()) {
                System.err.println("❌ ERRO: Relatório HTML não encontrado!");
                System.err.println("   Caminho esperado: " + htmlFile.getAbsolutePath());
                System.err.println();
                System.err.println("   Execute primeiro:");
                System.err.println("   mvn clean test jacoco:report");
                System.exit(1);
            }

            System.out.println("✅ Relatório HTML encontrado:");
            System.out.println("   " + htmlFile.getAbsolutePath());
            System.out.println();

            // 2. Criar diretório de saída se não existir
            File pdfFile = new File(PDF_OUTPUT_PATH);
            Path pdfPath = Paths.get(pdfFile.getParent());
            if (!Files.exists(pdfPath)) {
                Files.createDirectories(pdfPath);
                System.out.println("✅ Diretório criado: " + pdfPath.toAbsolutePath());
            }

            // 3. Converter HTML para PDF
            System.out.println("🔄 Convertendo HTML para PDF...");
            convertHtmlToPdf(htmlFile, pdfFile);
            System.out.println();

            // 4. Verificar se o PDF foi gerado
            if (pdfFile.exists()) {
                long fileSize = pdfFile.length();
                double fileSizeMB = fileSize / (1024.0 * 1024.0);
                System.out.println("✅ PDF gerado com sucesso!");
                System.out.println("   Arquivo: " + pdfFile.getAbsolutePath());
                System.out.println("   Tamanho: " + String.format("%.2f", fileSizeMB) + " MB");
                System.out.println();
                System.out.println("==========================================");
                System.exit(0);
            } else {
                System.err.println("❌ ERRO: PDF não foi gerado!");
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("❌ ERRO ao gerar PDF:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Converte um arquivo HTML para PDF usando Flying Saucer.
     * Preserva exatamente a mesma formatação, valores e organização do HTML.
     * 
     * @param htmlFile Arquivo HTML de entrada
     * @param pdfFile  Arquivo PDF de saída
     * @throws IOException Se houver erro de I/O
     */
    private static void convertHtmlToPdf(File htmlFile, File pdfFile) throws IOException {
        ITextRenderer renderer = new ITextRenderer();
        
        try {
            // Converter File para URI absoluto
            // O Flying Saucer resolve recursos relativos automaticamente baseado na URL do documento
            URI htmlUri = htmlFile.toURI();
            String htmlUrl = htmlUri.toString();
            
            // Normalizar URL para garantir que recursos relativos sejam encontrados
            // No Windows, file:///C:/path pode precisar ser file:///C|/path ou file://C:/path
            if (htmlUrl.startsWith("file:///") && htmlUrl.length() > 8 && Character.isLetter(htmlUrl.charAt(8))) {
                // Windows: file:///C:/path -> file:///C|/path
                htmlUrl = "file:///" + htmlUrl.substring(8, 9) + "|" + htmlUrl.substring(10);
            }
            
            // Configurar o documento HTML
            // O Flying Saucer automaticamente resolve recursos relativos (CSS, imagens) baseado na URL
            renderer.setDocument(htmlUrl);
            
            // Configurações para preservar exatamente a formatação do HTML
            renderer.getSharedContext().setDotsPerPixel(1); // Resolução padrão para melhor qualidade
            renderer.getSharedContext().setPrint(true); // Modo de impressão
            renderer.getSharedContext().setInteractive(false); // Não interativo (PDF)
            
            // Configurar para preservar cores e formatação
            renderer.getSharedContext().getTextRenderer().setSmoothingThreshold(0);
            
            // Renderizar PDF
            try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                renderer.layout();
                renderer.createPDF(outputStream);
            }
            
        } catch (Exception e) {
            throw new IOException("Erro ao converter HTML para PDF: " + e.getMessage(), e);
        } finally {
            renderer.finishPDF();
        }
    }
}

