import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MayerDiff {

    private MayerDiff(){}

    public static MayerDiff getInstance(){
        return new MayerDiff();
    }

    /**
     *
     * @param file1
     * @param file2
     * @return
     * @throws IOException
     */
    public ArrayList<String> compare(String file1, String file2) throws IOException {

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file1));
        BufferedReader bufferedReader1 = new BufferedReader(new FileReader(file2));

        String str = null;
        while ((str = bufferedReader.readLine()) != null){
            if (!str.trim().isEmpty())
                list1.add(str.trim());
        }

        while ((str = bufferedReader1.readLine()) != null ) {
            if (!str.trim().isEmpty())
                list2.add(str.trim());
        }
        bufferedReader.close();
        bufferedReader1.close();
        int n = list1.size();
        int m = list2.size();

        List<Integer> V = Arrays.asList(new Integer[2*(n+m)+1]);
        List<List<Integer>> list = new ArrayList<>();
        V.set(m+n+1,0);
        boolean stop = false;
        boolean lastDown = false;
        int lastSnake =0;
        int d = 0;
        int k =0;
        for ( d = 0 ; d <= n + m ; d++ )
        {
            for (  k = -d ; k <= d ; k += 2 )
            {
                // down or right?
                boolean down = ( k == -d || ( k != d && V.get( n+m+k - 1 ) < V.get( n+m+k + 1 ) ) );

                int kPrev = down ? k + 1 : k - 1;

                // start point
                int xStart = V.get( n+m+kPrev );

                // mid point
                int xMid = down ? xStart : xStart + 1;
                int yMid = xMid - k;

                // end point
                int xEnd = xMid;
                int yEnd = yMid;

                // follow diagonal
                int snake = 0;
                while ( xEnd < n && yEnd < m && list1.get(xEnd).equals(list2.get(yEnd)) ) { xEnd++; yEnd++; snake++; }

                // save end point
                V.set( n+m+k, xEnd);

                // check for solution
                if ( xEnd >= n && yEnd >= m ){
                    stop=true;
                    lastSnake = snake;
                    lastDown = down;
                    break;
                } /* solution has been found */

            }
            list.add(V.stream().collect(Collectors.toList()));
            if (stop){
                break;
            }

        }

        ArrayList<String> lcs = new ArrayList<>();
        int xEnd = list.get(d).get(n+m+k);
        int yEnd =  xEnd - k;
        
        if (lastSnake!=0){

            while (lastSnake !=0){
                lcs.add(list1.get(xEnd-1));
                //System.out.println(list1.get(xEnd-1));
                xEnd--;
                yEnd--;
                lastSnake--;
            }
        }
        if (lastDown){
            k++;
            d--;

        } else {
            k--;
            d--;

        }

        xEnd = list.get(d).get(m+n+k);
        yEnd = xEnd - k;

        int lastXEnd = 0;
        int lastYEnd = 0;
        while (d>0){
 
            lastDown = ( k == -d || ( k != d && list.get(d-1).get( n+m+k - 1 ) < list.get(d-1).get( n+m+k + 1 ) ) );

            int kPrev = lastDown ? k + 1 : k - 1;
 
            lastXEnd = lastDown ? list.get(d-1).get(m+n+kPrev) : list.get(d-1).get(m+n+kPrev)+1;
            lastYEnd = lastXEnd - k;

            if ((xEnd-lastXEnd) == (yEnd- lastYEnd)) {
                lastSnake = xEnd - lastXEnd;
            }

            if (lastSnake!=0){
                while (lastSnake!=0){
                    //System.out.println("Appending: "+list1.get(xEnd-1));
                    lcs.add(list1.get(xEnd-1));
                    xEnd--;
                    yEnd--;
                    lastSnake--;
                }
            }
            k = kPrev;
            d--;
            xEnd = list.get(d).get(m+n+kPrev);
            yEnd = xEnd - k;
            //System.out.println("xEnd: "+xEnd+" yEnd: "+yEnd);
        }
        if(d==0 && xEnd != 0 ){
            while (xEnd>0){
                lcs.add(list1.get(xEnd-1));
                xEnd--;
            }
        }

        return lcs;
    }
}
