<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Liste de toutes les fédérations sportives
     Utilise la variable ${federationChoisie} pour pré-sélectionner. --%>

<option value="101" ${federationChoisie == '101' ? 'selected' : ''}>FF d'Athlétisme</option>
<option value="102" ${federationChoisie == '102' ? 'selected' : ''}>FF d'Aviron</option>
<option value="103" ${federationChoisie == '103' ? 'selected' : ''}>FF de Badminton</option>
<option value="105" ${federationChoisie == '105' ? 'selected' : ''}>FF de Basketball</option>
<option value="106" ${federationChoisie == '106' ? 'selected' : ''}>FF de Boxe</option>
<option value="107" ${federationChoisie == '107' ? 'selected' : ''}>FF de Canoë-Kayak</option>
<option value="108" ${federationChoisie == '108' ? 'selected' : ''}>FF de Cyclisme</option>
<option value="109" ${federationChoisie == '109' ? 'selected' : ''}>FF d'Équitation</option>
<option value="110" ${federationChoisie == '110' ? 'selected' : ''}>FF d'Escrime</option>
<option value="111" ${federationChoisie == '111' ? 'selected' : ''}>FF de Football</option>
<option value="112" ${federationChoisie == '112' ? 'selected' : ''}>FF des Sports de Glace</option>
<option value="113" ${federationChoisie == '113' ? 'selected' : ''}>FF de Gymnastique</option>
<option value="114" ${federationChoisie == '114' ? 'selected' : ''}>FF d'Haltérophilie</option>
<option value="115" ${federationChoisie == '115' ? 'selected' : ''}>FF de Handball</option>
<option value="116" ${federationChoisie == '116' ? 'selected' : ''}>FF de Hockey</option>
<option value="117" ${federationChoisie == '117' ? 'selected' : ''}>FF de Judo</option>
<option value="118" ${federationChoisie == '118' ? 'selected' : ''}>FF de Lutte</option>
<option value="119" ${federationChoisie == '119' ? 'selected' : ''}>FF de Natation</option>
<option value="120" ${federationChoisie == '120' ? 'selected' : ''}>FF de Pentathlon Moderne</option>
<option value="121" ${federationChoisie == '121' ? 'selected' : ''}>FF de Ski</option>
<option value="122" ${federationChoisie == '122' ? 'selected' : ''}>FF de Taekwondo</option>
<option value="123" ${federationChoisie == '123' ? 'selected' : ''}>FF de Tennis</option>
<option value="124" ${federationChoisie == '124' ? 'selected' : ''}>FF de Tennis de Table</option>
<option value="125" ${federationChoisie == '125' ? 'selected' : ''}>FF de Tir</option>
<option value="126" ${federationChoisie == '126' ? 'selected' : ''}>FF de Tir à l'Arc</option>
<option value="127" ${federationChoisie == '127' ? 'selected' : ''}>FF de Triathlon</option>
<option value="128" ${federationChoisie == '128' ? 'selected' : ''}>FF de Voile</option>
<option value="129" ${federationChoisie == '129' ? 'selected' : ''}>FF de Volley</option>
<option value="131" ${federationChoisie == '131' ? 'selected' : ''}>FF de Hockey sur Glace</option>
<option value="132" ${federationChoisie == '132' ? 'selected' : ''}>FF de Golf</option>
<option value="133" ${federationChoisie == '133' ? 'selected' : ''}>FF de Rugby</option>
<option value="135" ${federationChoisie == '135' ? 'selected' : ''}>FF de la Montagne et de l'Escalade</option>
<option value="136" ${federationChoisie == '136' ? 'selected' : ''}>FF de Roller et Skateboard</option>
<option value="137" ${federationChoisie == '137' ? 'selected' : ''}>FF de Surf</option>
<option value="138" ${federationChoisie == '138' ? 'selected' : ''}>FF de Baseball, Softball</option>
<option value="139" ${federationChoisie == '139' ? 'selected' : ''}>FF de Danse</option>
<option value="140" ${federationChoisie == '140' ? 'selected' : ''}>FF de Football Américain</option>
<option value="141" ${federationChoisie == '141' ? 'selected' : ''}>FF de Squash</option>
<option value="201" ${federationChoisie == '201' ? 'selected' : ''}>FF d'Aéromodélisme</option>
<option value="202" ${federationChoisie == '202' ? 'selected' : ''}>FF Aéronautique</option>
<option value="203" ${federationChoisie == '203' ? 'selected' : ''}>FF d'Aérostation</option>
<option value="204" ${federationChoisie == '204' ? 'selected' : ''}>FF d'Aïkido et Affinitaires</option>
<option value="205" ${federationChoisie == '205' ? 'selected' : ''}>FF d'Aïkido et de Budo</option>
<option value="206" ${federationChoisie == '206' ? 'selected' : ''}>FF du Sport Automobile</option>
<option value="208" ${federationChoisie == '208' ? 'selected' : ''}>FF de Jeu de Balle au Tambourin</option>
<option value="209" ${federationChoisie == '209' ? 'selected' : ''}>FF de Ball-Trap</option>
<option value="210" ${federationChoisie == '210' ? 'selected' : ''}>FF de Billard</option>
<option value="211" ${federationChoisie == '211' ? 'selected' : ''}>FF du Sport Boules</option>
<option value="212" ${federationChoisie == '212' ? 'selected' : ''}>FF de Savate, Boxe Française</option>
<option value="213" ${federationChoisie == '213' ? 'selected' : ''}>FF de Bowling</option>
<option value="214" ${federationChoisie == '214' ? 'selected' : ''}>FF de Char à Voile</option>
<option value="215" ${federationChoisie == '215' ? 'selected' : ''}>FF de Course Camarguaise</option>
<option value="216" ${federationChoisie == '216' ? 'selected' : ''}>FF de la Course Landaise</option>
<option value="217" ${federationChoisie == '217' ? 'selected' : ''}>FF de Course d'Orientation</option>
<option value="218" ${federationChoisie == '218' ? 'selected' : ''}>FF de Cyclotourisme</option>
<option value="220" ${federationChoisie == '220' ? 'selected' : ''}>FF des Échecs</option>
<option value="221" ${federationChoisie == '221' ? 'selected' : ''}>FF d'Études et Sports Sous-Marins</option>
<option value="224" ${federationChoisie == '224' ? 'selected' : ''}>FF d'Hélicoptère</option>
<option value="226" ${federationChoisie == '226' ? 'selected' : ''}>FF de Javelot Tir sur Cible</option>
<option value="227" ${federationChoisie == '227' ? 'selected' : ''}>FF de Jeu de Paume</option>
<option value="228" ${federationChoisie == '228' ? 'selected' : ''}>FF de Joute et Sauvetage Nautique</option>
<option value="229" ${federationChoisie == '229' ? 'selected' : ''}>FF de Karaté</option>
<option value="231" ${federationChoisie == '231' ? 'selected' : ''}>FF de Longue Paume</option>
<option value="233" ${federationChoisie == '233' ? 'selected' : ''}>FF de Motocyclisme</option>
<option value="234" ${federationChoisie == '234' ? 'selected' : ''}>FF Motonautique</option>
<option value="237" ${federationChoisie == '237' ? 'selected' : ''}>FF de Parachutisme</option>
<option value="241" ${federationChoisie == '241' ? 'selected' : ''}>FF de Pelote Basque</option>
<option value="244" ${federationChoisie == '244' ? 'selected' : ''}>FF de Pulka et Traîneau à Chiens</option>
<option value="248" ${federationChoisie == '248' ? 'selected' : ''}>FF de Rugby à XIII</option>
<option value="249" ${federationChoisie == '249' ? 'selected' : ''}>FF de Sauvetage et Secourisme</option>
<option value="250" ${federationChoisie == '250' ? 'selected' : ''}>FF de Ski Nautique et Wakeboard</option>
<option value="251" ${federationChoisie == '251' ? 'selected' : ''}>FF Spéléologie</option>
<option value="254" ${federationChoisie == '254' ? 'selected' : ''}>FF des Arts Énergétiques et Martiaux</option>
<option value="257" ${federationChoisie == '257' ? 'selected' : ''}>FF de Vol en Planeur</option>
<option value="258" ${federationChoisie == '258' ? 'selected' : ''}>FF de Vol Libre</option>
<option value="260" ${federationChoisie == '260' ? 'selected' : ''}>FF de Polo</option>
<option value="261" ${federationChoisie == '261' ? 'selected' : ''}>FF de Kick Boxing</option>
<option value="263" ${federationChoisie == '263' ? 'selected' : ''}>FF de Double Dutch</option>
<option value="264" ${federationChoisie == '264' ? 'selected' : ''}>FF de Flying Disc</option>
<option value="266" ${federationChoisie == '266' ? 'selected' : ''}>FF de Force</option>
<option value="267" ${federationChoisie == '267' ? 'selected' : ''}>FF des Pêches Sportives</option>
<option value="402" ${federationChoisie == '402' ? 'selected' : ''}>FF Éducation Physique et Gym Volontaire</option>
<option value="403" ${federationChoisie == '403' ? 'selected' : ''}>FF Sports Pour Tous</option>
<option value="404" ${federationChoisie == '404' ? 'selected' : ''}>FF de la Retraite Sportive</option>
<option value="409" ${federationChoisie == '409' ? 'selected' : ''}>F Maccabi</option>
<option value="420" ${federationChoisie == '420' ? 'selected' : ''}>F Sportive des ASPTT</option>
<option value="501" ${federationChoisie == '501' ? 'selected' : ''}>FF Handisport</option>
<option value="503" ${federationChoisie == '503' ? 'selected' : ''}>FF du Sport Adapté</option>
<option value="601" ${federationChoisie == '601' ? 'selected' : ''}>FF du Sport Universitaire</option>
<option value="602" ${federationChoisie == '602' ? 'selected' : ''}>F Sportive Educative de l'Enseignement</option>
<option value="603" ${federationChoisie == '603' ? 'selected' : ''}>Union Nationale des Clubs Universitaires</option>
<option value="604" ${federationChoisie == '604' ? 'selected' : ''}>Union Nationale du Sport Scolaire</option>
