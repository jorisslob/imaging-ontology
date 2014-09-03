Example
=======

* Achilles is a biologist at the Bellerophon institute.
* Castor is an analist at the Daedalus institute.
* Eleusis is the supervisor of Achilles.
* Laertes is the supervisor of Castor.
* The Ganymede2000 is an expensive microscope used at the Bellerophon institute.
* Achilles studies embryo Hydras.
* Achilles has acquired an Image on the Ganymede2000 of one of the Hydra embryos, during a measurement.
* This Image is sent to Castor to analyse.
* Caster uses JImage to analyse the images and create a K-diagram out of them.

* Achilles, Person A
* Bellerophon institute, Organization B
* Castor, Person C
* Daedalus institute, Organization D
* Eleusis, Person E
* Laertes, Person L
* Ganymede2000,	Microscope G1 with SoftwareAgent G2
* Hydra embryo, Entity H
* Image, Entity I
* JImage, SoftwareAgent J
* K-diagram, Entity K
* Analysis, Activity N
* Measurement, Activity M

A actedOnBehalfOf E
C actedOnBehalfOf L
E actedOnBehalfOf B
L actedOnBehalfOf D
G1 wasAttributedTo B
H wasAttributedTo A
M used H
M used G1
M wasAssociatedWith G2
G2 actedOnBehalfOf A
I wasGeneratedBy M
N used I
N wasAssociatedWith J
J actedOnBehalfOf C
K hadPrimarySource I
K wasAttributedTo J
K wasGeneratedBy N