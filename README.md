When the app is opened, a set of visual and auditory instructions is presented to the admin.
Instructions include pointing the camera at the floor initially and then slowly raising it
toward the room number to precisely calibrate the location of the floor and the room number
by the doorway. The app will interpret the room number and input it as text, indicated by a
green text sign once it is identified through ML Kit. After that, directions to other rooms
will be mapped by adding nodes and paths until reaching another doorway. Once a path has been
set, the next room number can be assigned as an entry using the entry button. This information
will be stored in a local database and can be accessed later by users to determine their start
and end points. This navigation algorithm uses Google Play Services for AR, allowing the app to
utilize ARCore for Augmented Reality Indoor Navigation (ARIN).