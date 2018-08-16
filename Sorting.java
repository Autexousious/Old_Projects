/*
Author: Alexander Rickford
Purpose: illustrate different sorting methods
*/
import java.text.DecimalFormat;
import java.util.Arrays;

public class Sorting {
	//method that emplys the bubble sort algorithm to sort data
	//@ long[]: a - given array to be sorted
	public static void BubbleSort(long[] a) {
		for(int count = 0; count < a.length; count++){
			for(int c = 0; c < a.length - (count + 1); c++){
				int i = c + 1;
				if(a[c] > a[i]){
					long temp = a[i];
					a[i] = a[c];
					a[c] = temp;
				}
			}//for
		}//for
	}//BubbleSort()
	//method that employs the merge sort algorithm in a non recursive
	//way. It creates an auxillary array to pass the data back and forth
	//@ long[]: a - given array to be sorted
	public static void MergeSortNonRec(long[] a) {
		long[] tempArray = new long[a.length];
		long[] ref1, ref2;
		for(int count = 1, swap = 0; count < a.length ; count *= 2){
			if(swap == 0){
				ref1 = a;
				ref2 = tempArray;
				swap = 1;
			}
			else{
				ref1 = tempArray;
				ref2 = a;
				swap = 0;
			}
			for(int c = 0, i = c + count; i < a.length; i = (c += 2 * count) + count){
				for(int index = c, p1 = c, p2 = i; index < a.length && index < i + count; index++){
					if(p1 > i) ref1[index] = ref2[p2++];
					else if(p2 > (i + (count - 1))) ref1[index] = ref2[p1++];
					else if (ref2[p1] > ref2[p2]) ref1[index] = ref2[p2];
					else ref1[index] = ref2[p1];
				}//for
			}//for
			if(ref1 == tempArray)
				for(int c = 0; c < a.length; c++){
					a[c] = ref1[c];
				}
		}//for
	}//MergeSortNonRec()
	
	//-------------------------------------------------------------
	//---------- Below is an implementation of Selection Sort -----
	//-------------------------------------------------------------		
	public static void SelectionSort(long[] a) {
		int N = a.length;
	    for (int i = 0; i < N; i++) {
	    	int min = i;
	        for (int j = i+1; j < N; j++) {
	        	if (a[j] < a[min]) min = j;
	        }//for
	        exch(a, i, min);
	    }//for
	}//SelectionSort()
	
	
	//-----------------------------------------------------------------------
	//---------- Below is an implementation of Insertion Sort ----------
	//-----------------------------------------------------------------------
	public static void InsertionSort(long[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            for (int j = i; j > 0 && a[j] < a[j-1]; j--) {
                exch(a, j, j-1);
            }//for
        }//for
	}//InsertionSort()

	//-----------------------------------------------------------------------
	//---------- Below is an implementation of recursive MergeSort ----------
	//-----------------------------------------------------------------------
	
    private static void merge(long[] a, long[] aux, int lo, int mid, int hi) {

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k]; 
        }//for

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)           a[k] = aux[j++];
            else if (j > hi)            a[k] = aux[i++];
            else if (aux[j] < aux[i]) 	a[k] = aux[j++];
            else                        a[k] = aux[i++];
        }//for
    }

    
    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static void sort(long[] a, long[] aux, int lo, int hi) {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid + 1, hi);
        merge(a, aux, lo, mid, hi);
    }

    public static void MergeSort(long[] a) {
        long[] aux = new long[a.length];
        sort(a, aux, 0, a.length-1);
    }
    
	//------------------------------------------------------
	//---------- below are several helper methods ----------
	//------------------------------------------------------
	
	// This tests whether your sorted result is correct by comparing it to reference result
	public static boolean testSort(long[] a) {
		long[] a2 = new long[a.length];
		System.arraycopy(a, 0, a2, 0, a.length);
		Arrays.sort(a);
		for(int i = 0; i < a.length; i++)
			if(a2[i] != a[i]) 
				return false;
		return true;
	}//testSort()
	
	
	// This creates an array with n randomly generated elements between (0, n*10]
	private static long[] randArray(int n) {
		long[] rand = new long[n];
		for(int i=0; i<n; i++)
			rand[i] = (int) (Math.random() * n * 10);
		return rand;
	}//randArray()
	
	private static void startTimer() { 
		timestamp = System.nanoTime();
	}//startTimer()
	
	private static double endTimer() {
		return (System.nanoTime() - timestamp)/1000000.0;
	}//endTimer()
	        
    // exchange a[i] and a[j]
    private static void exch(long[] a, int i, int j) {
        long swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
	
	private static long timestamp;
	
	//---------------------------------------------
	//---------- This is the main method ----------
	//---------------------------------------------		
	public static void main(String[] args) {
	
		// run experiments
		final int BUBBLE = 0, SELECT = 1, INSERT = 2, MERGEREC = 3, MERGENONREC = 4;
		int[] algorithms = {BUBBLE, SELECT, INSERT, MERGEREC, MERGENONREC};
		
		// max defines the maximum size of the array to be tested, which is 2^max
		// runs defines the number of rounds to be performed per test, in order to get an average running time.
		int max = 14, runs = 5;
		double[][] stats = new double[algorithms.length][max];
		for(int i=0; i<algorithms.length; i++) {             //loop through each sorting algorithm
			switch(i) {
				case BUBBLE: System.out.print("Running Bubble Sort ..."); break;
				case SELECT: System.out.print("Running Selection Sort ..."); break;
				case INSERT: System.out.print("Running Insertion Sort ..."); break;
				case MERGEREC: System.out.print("Running MergeSort Recursive ..."); break;
				case MERGENONREC: System.out.print("Running MergeSort Non Recursive ..."); break;
			}//switch
			for(int j=0; j<max; j++) {        //loop through each array size 
				double avg = 0;
				for(int k=0; k<runs; k++) {    // loop through each run
					long[] a = randArray((int) Math.pow(2, j+1));
					startTimer();
					switch(i) {
						case BUBBLE: BubbleSort(a); break;
						case SELECT: SelectionSort(a); break;
						case INSERT: InsertionSort(a); break;
						case MERGEREC: MergeSort(a); break;
						case MERGENONREC: MergeSortNonRec(a); break;
					}//switch
					avg += endTimer();
					if (testSort(a) == false)
						System.out.println("The sorting is INCORRECT!" + "(N=" + a.length + ", round=" + k + ").");
				}//for
				avg /= runs;
				stats[i][j] = avg;
			}//for
			System.out.println("done.");
		}//for
		
		DecimalFormat format = new DecimalFormat("0.0000");
		System.out.println();
		System.out.println("Average running time:");
		System.out.println("N\t Bubble Sort\t Selection Sort\t Insertion Sort\tMergeSortRec\tMergeSortNon");
		for(int i=0; i<stats[0].length; i++) {
			System.out.print((int) Math.pow(2, i+1) + "\t  ");
			for(int j=0; j<stats.length; j++) {
				System.out.print(format.format(stats[j][i]) + "\t  ");
			}//for
			System.out.println();
		}//for
	}//main()
	
}//end of class
