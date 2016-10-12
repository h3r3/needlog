package org.postronic.h3.needlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.postronic.h3.needlog.textlines.AllTextLines;
import org.postronic.h3.needlog.textlines.SearchResultsTextLines;
import org.postronic.h3.needlog.textlines.TextLines;
import org.postronic.h3.needlog.ui.MainWindow;

public class Needlog {
	
	public static final String VERSION = "v.0.2 (alpha)";
	
	private volatile float percentIndexing = 0.0f;
	private IndexWriter indexWriter;
	
	private Needlog(File indexDir) {
		try {
			indexWriter = new IndexWriter(FSDirectory.open(indexDir),
					new SimpleAnalyzer(), 
					true, 
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public float getPercentIndexing() {
		return percentIndexing;
	}
	
	private void index(File textFile) {
		try {
			long filePos = 0;
			long fileLength = textFile.length();
		    BufferedReader input =  new BufferedReader(new FileReader(textFile));
		    try {
		    	String line = null;
		    	int lineCount = 1;
		    	while ((line = input.readLine()) != null){
		    		Document doc = new Document();
		    		doc.add(new Field("line", Integer.toString(lineCount++), Field.Store.YES, Field.Index.NOT_ANALYZED));
		 		    doc.add(new Field("text", line, Field.Store.YES, Field.Index.ANALYZED));
		 		    indexWriter.addDocument(doc); // TODO User multiple threads on the same indexWriter to increase speed
		 		    filePos += line.length();
		 		    percentIndexing = Math.min((float) filePos * 100.0f / (float) fileLength, 99.999f);
		    	}
		    }
		    finally {
		    	input.close();
		    	percentIndexing = 100.0f;
		    }
			indexWriter.optimize();
			//indexWriter.close();
		} catch (Exception e) {
			throw new RuntimeException("Indexing failed. Details: " + e.getMessage(), e);
		}
	}
	
	public TextLines search(String queryString) {
//			System.out.println("Search [" + queryString + "]");
//			long startTimeMillis = System.currentTimeMillis();
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try {
			indexReader = indexWriter.getReader();
			TextLines result;
			if (queryString.trim().length() == 0) {
				result = new AllTextLines(indexReader);
			} else {
			    indexSearcher = new IndexSearcher(indexReader);
			    NeedlogQuery texterQuery = new NeedlogQuery(queryString);
			    Sort sort = new Sort(new SortField("line", SortField.INT));
			    TopDocs topDocs = indexSearcher.search(texterQuery, null, 25000, sort);	    
			    result = new SearchResultsTextLines(indexSearcher, topDocs);
			}
//			System.out.println("Searched in " + (System.currentTimeMillis() - startTimeMillis) + " ms");
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Search failed - details: " + e.getMessage(), e);
		} finally {
			try {
				if (indexSearcher != null) { indexSearcher.close(); }
			} catch (IOException e) {
				throw new RuntimeException("IndexSearcher close failed - details: " + e.getMessage(), e);
			} finally {
//				try {
//					if (indexReader != null) { indexReader.close(); }
//				} catch (IOException e) {
//					throw new RuntimeException("Reader close failed - details: " + e.getMessage(), e);
//				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (args.length != 1) {
			System.out.println("*** Needlog " + VERSION);
			System.out.println();
			System.out.println("If you are using Needlog.exe:");
			System.out.println("Usage: Needlog filename");
			System.out.println();
			System.out.println("If you are using Needlog.jar:");
			System.out.println("Usage: java -jar Needlog.jar filename");
			JOptionPane.showMessageDialog(null, "Usage: you must drop a text file on the Needlog icon", "Needlog " + VERSION, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		File inputFile = new File(args[0]); 
		String tempDir = System.getProperty("java.io.tmpdir");
		if (!tempDir.endsWith(File.separator)) { tempDir += File.separator; }
		final File indexDir = new File(tempDir + "TexterIndex.tmp");
		indexDir.mkdir();
		
		Needlog needlog = new Needlog(indexDir);
		
		final IndexWriter indexWriterFinal = needlog.indexWriter;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					indexWriterFinal.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				deleteDirectory(indexDir); 
			}
		}, "Delete temp directory"));
		

		MainWindow mainWindow = new MainWindow(needlog);
		mainWindow.setVisible(true);
		
		IndexingFeedbackRunnable indexingFeedbackRunnable = new IndexingFeedbackRunnable(needlog, mainWindow);
		Thread indexingFeedbackThread = new Thread(indexingFeedbackRunnable, "Indexing Feedback");
		indexingFeedbackThread.setDaemon(true);
		indexingFeedbackThread.start();
		
		needlog.index(inputFile);
	}
	
	private static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(new File(directory, children[i]));
                if (!success) { return false; }
            }
        }
        return directory.delete();
    }
	
}
