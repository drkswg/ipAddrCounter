# ipAddrCounter


### A utility that counts the number of unique IP addresses in a text file (but can be used to count unique lines in any text file).

For files up to 512 megabytes, an in-memory algorithm is used; for larger files, an external sort algorithm is employed. 
The file size is only limited by the available space on your computer
(The external sort algorithm creates temporary files with a combined size approximately equal to the original file, so it's nice to have free space equal the size of the original file).

You need to provide path to your text file as args[0], аfter the program finishes its execution, it will display the number of unique lines and the execution time in seconds.
