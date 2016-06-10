package administration.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import catalog.Detail;
import catalog.Item;
import database.DBTablePrinter;
import database.Database;

public class Report {
	
	public static ResultSet getProdottiInEsaurimento(){
		ResultSet result = null;
		
		try {
			result = Database.executeQuery(prodottiInEsaurimento);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static ArrayList<Item> getProdottiPerFasciaPrezzo(int min, int max){
		ResultSet scarpeResultSet = null,immaginiResultSet, dettagliResultSet;;
		
		PreparedStatement ps = Database.getPreparedStatement(prodottiPerFasciaPrezzo);
		
		try {
			ps.setInt(1, min);
			ps.setInt(2, max);
			scarpeResultSet = ps.executeQuery();
			DBTablePrinter.printResultSet(scarpeResultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ArrayList<Item> productsList = new ArrayList<>();
		try {
			while (scarpeResultSet.next()) {
				int id = scarpeResultSet.getInt("idScarpe");
				String marca = scarpeResultSet.getString("marca");
				String modello = scarpeResultSet.getString("modello");
				int prezzo_vendita = scarpeResultSet.getInt("prezzo_vendita");
				int prezzo_acquisto = scarpeResultSet.getInt("prezzo_acquisto");
				int quantitaDisp = scarpeResultSet.getInt("quantitaDisp");
				int scorta_minima = scarpeResultSet.getInt("scorta_minima");
				String alt = scarpeResultSet.getString("alt");
				String descrizione = scarpeResultSet.getString("descrizione");
				/**********************************************/

				ArrayList<String> images = new ArrayList<>();
				immaginiResultSet = Database.executeQuery("SELECT * FROM immagini WHERE scarpa = " + id + ";");

				while (immaginiResultSet.next()) {
					images.add(immaginiResultSet.getString("url"));
				}

				/***********************************************/
				ArrayList<Detail> dettagli = new ArrayList<>();
				dettagliResultSet = Database.executeQuery("SELECT * FROM dettagli WHERE scarpa = " + id + ";");

				while (dettagliResultSet.next()) {
					String currentIntestazione = dettagliResultSet.getString("intestazione");
					String currentCorpo = dettagliResultSet.getString("corpo");

					Detail currentDetail = new Detail(currentIntestazione, currentCorpo);

					dettagli.add(currentDetail);
				}

				Item currentItem = new Item(id, marca, modello, prezzo_vendita, prezzo_acquisto, quantitaDisp,
						scorta_minima, images, alt, descrizione, dettagli);

				// System.out.println("Current Item: " + currentItem);
				productsList.add(currentItem);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productsList;
	}
	
	private static String prodottiInEsaurimento;
	private static String prodottiPerFasciaPrezzo;
	
	static {
		prodottiInEsaurimento = " SELECT * "
							  + " FROM scarpe "
							  + " WHERE quantitaDisp < scorta_minima;";
		
		prodottiPerFasciaPrezzo = " SELECT * "
								+ " FROM scarpe "
								+ " WHERE prezzo_vendita BETWEEN ? AND ?;";
	}

}
