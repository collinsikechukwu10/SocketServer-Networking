## Advanced Features Implemented:
### - Implemented POST request.
 There was no clear direction on how we should implement other
request methods. 
 I initially decided to implement storing file uploads from a multipart form
to specific folder called uploads in the path that serves the static pages provided during command line initialization of the server.
However, there were issues extracting the data from the request as i had issues seperating each form field using the boundary included in the content type.
Hence, I decided to implement something simpler, It would just print out the data received from non multipart requests to the console/file
### - Implemented DELETE request.
### - Added logging for requests
The logs are printed on the console and also stored in a file called "server.log".
It includes the request date/time, request type, and also  response code.
### - Returning of binary images (GIF, JPEG and PNG).