Brandon Toops, Patrick Phillips, Qingjie Liu

Run the Main.java method in powershell with the command:

java -cp ".\bin\" Main fileName number QueryVariable Evidence

ex: java -cp ".\bin\" Main aima-wet-grass.xml - C S true

Occassionally, the ordering of the true and false are switched in the output, but the values are correct.

ex: 

The exact inference is
{true=0.16666666666666669, false=0.8333333333333334}

The Rejection sampling inference is
{false=0.8111111111111111, true=0.18888888888888888}

The Likelihood Weighting Inference is
{true=0.18402777777777918, false=0.815972222222221}

notice how Rejection Sampling sometimes prints false, true while likelihood weighting prints true, false, but both represent approximately equivalent values for true and false.