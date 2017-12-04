import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CryptoCurrency {
  
    public static void main(String[] args) throws Exception {
        int cryptoCurrencyNumber = 10;
        
        ArrayList<Coin> lCoins = readCryptoCurrency();
        
        for(int i = 0; i < cryptoCurrencyNumber; i++){
            getHistoricalData(lCoins.get(i).name, "20161201", "20171201", "C:\\Temp\\Crypto\\");
        }
    }
    
    public static ArrayList<Coin> readCryptoCurrency() throws Exception {
        URL coinMarketCap = new URL("https://coinmarketcap.com/coins/views/all/");
        String pag = Util.InvocaServlet(coinMarketCap);
        ArrayList<Coin> lCoins = new ArrayList<Coin>();
       
        pag = pag.substring(pag.indexOf("tbody") + 6);
        pag = pag.substring(0, pag.indexOf("tbody") - 2);
        
        
        String[] coins = pag.split("<tr");
        
        for(int i = 0; i < coins.length; i++){
            
            String coin = coins[i].trim();
            
            if(coin.length() > 6){
                coin = coin.substring(7);     
                String nCoin = coin.substring(0, coin.indexOf("\""));                
                String[] fields = coin.split("<td");
                                
                if(fields[4].indexOf("$") > 0){
                    String marketValueT = fields[4].substring(fields[4].indexOf("$"));
                    marketValueT = marketValueT.substring(0, marketValueT.indexOf("<")).trim();
                    
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    Number marketValue = format.parse(marketValueT);
                    
                    Coin c = new Coin();
                    c.name = nCoin;
                    c.marketValue = marketValue;
                    
                    lCoins.add(c);
                    
                }
            }
        }
        
        Collections.sort(lCoins, new Comparator<Coin>(){
            public int compare(Coin c1, Coin c2){
            if(c1.marketValue == c2.marketValue)
                return 0;
            return c1.marketValue.doubleValue() > c2.marketValue.doubleValue() ? -1 : 1;
            }
        });
        
        return lCoins;        
    }
    
    public static void getHistoricalData(String coinName, String ini, String end, String path) throws Exception{
        URL coinMarketCap = new URL("https://coinmarketcap.com/currencies/" + coinName + "/historical-data/?start=" + ini + "&end=" + end);
        String pag = Util.InvocaServlet(coinMarketCap);
        
        pag = pag.substring(pag.indexOf("tbody") + 6);
        pag = pag.substring(0, pag.indexOf("tbody") - 2);        
        String[] days = pag.split("<tr");
        
        
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + coinName + ".csv"), "utf-8"));
        writer.write("Year, Month, Day, Open, High, Low, Close, Volume, Market Cap\n");
        
        for(int i = 0; i < days.length; i++){     
            
            String day = days[i].trim();
            
            if(day.length() > 0){                                
                String[] fields = day.split("<td");
                String date = fields[1].substring(fields[1].indexOf(">") + 1, fields[1].indexOf("<"));
                String open = fields[2].substring(fields[2].indexOf(">") + 1, fields[2].indexOf("<"));
                String high = fields[3].substring(fields[3].indexOf(">") + 1, fields[3].indexOf("<"));
                String low = fields[4].substring(fields[4].indexOf(">") + 1, fields[4].indexOf("<"));
                String close = fields[5].substring(fields[5].indexOf(">") + 1, fields[5].indexOf("<"));
                String volumeT = fields[6].substring(fields[6].indexOf(">") + 1, fields[6].indexOf("<"));
                String marketCapT = fields[7].substring(fields[7].indexOf(">") + 1, fields[7].indexOf("<"));
                
                NumberFormat format = NumberFormat.getCurrencyInstance();
                Number volume = 0;
                Number marketCap = 0;
                
                try{                
                    marketCap = format.parse("$" + marketCapT);
                }catch(Exception e){                    
                };
                
                try{                
                    volume = format.parse("$" + volumeT);
                }catch(Exception e){                    
                };
                
                String year = date.substring(8);
                String month = date.substring(0, 3);
                String dayT = date.substring(date.indexOf(" ") + 1, date.indexOf(","));
                                
                writer.write(year + "," + month + "," + dayT + "," + open + "," + high + "," + low + "," + close + "," + volume + "," + marketCap + "\n");
                
            }
            
        }
        
        writer.close();
    }
    
}
