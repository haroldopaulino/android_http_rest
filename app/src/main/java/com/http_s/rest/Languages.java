package com.http_s.rest;

import org.json.JSONException;
import org.json.JSONObject;

public class Languages {

    private JSONObject
            englishObject,
            frenchObject,
            spanishObject,
            portugueseObject;

    Languages() {
        _loadValues();
    }

    public String getText(String language, String item) {
        String output = "";
        try {
            switch(language.toUpperCase().trim()) {
                case "" :
                case "0" :
                case "ENGLISH" :
                    output = englishObject.getString(item);
                    break;
                case "1" :
                case "SPANISH" :
                    output = spanishObject.getString(item);
                    break;
                case "2" :
                case "FRENCH" :
                    output = frenchObject.getString(item);
                    break;
                case "3" :
                case "PORTUGUESE" :
                    output = portugueseObject.getString(item);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    private void _loadValues() {
        _loadEnglishValues();
        _loadFrenchValues();
        _loadSpanishValues();
        _loadPortugueseValues();
    }

    private void _loadEnglishValues() {
        try {
            englishObject = new JSONObject();
            englishObject.put("PRIVACY_POLICY", "Privacy Policy");
            englishObject.put("PRIVACY_POLICY_CONTENTS",
                            "Haroldo Paulino built the HTTP REST app as a Free app. This SERVICE is provided by Haroldo Paulino at no cost and is intended for use as is.\n" +
                                    "\n" +
                                    "This policy is used to inform App visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.\n" +
                                    "\n" +
                                    "If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                                    "\n" +
                                    "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at HTTP REST unless otherwise defined in this Privacy Policy.\n" +
                                    "\n" +
                                    "Information Collection and Use\n" +
                                    "\n" +
                                    "For a better experience, while using our Service, I may require you to provide us with certain personally identifiable information, including but not limited to Access to the Device State and Contacts. The information that I request is retained on your device and is not collected by me in any way\n" +
                                    "\n" +
                                    "The app does use third party services that may collect information used to identify you.\n" +
                                    "\n" +
                                    "Link to privacy policy of third party service providers used by the app\n" +
                                    "\n" +
                                    "Google Play Services\n" +
                                    "Log Data\n" +
                                    "\n" +
                                    "I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (???IP???) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.\n" +
                                    "\n" +
                                    "Service Providers\n" +
                                    "\n" +
                                    "I may employ third-party companies and individuals due to the following reasons:\n" +
                                    "\n" +
                                    "* To facilitate our Service;\n" +
                                    "* To provide the Service on our behalf;\n" +
                                    "* To perform Service-related services; or\n" +
                                    "* To assist us in analyzing how our Service is used.\n" +
                                    "* I want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                                    "\n" +
                                    "Security\n" +
                                    "\n" +
                                    "I value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and I cannot guarantee its absolute security.\n" +
                                    "\n" +
                                    "Links to Other Sites\n" +
                                    "\n" +
                                    "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                                    "\n" +
                                    "Children???s Privacy\n" +
                                    "\n" +
                                    "These Services do not address anyone under the age of 18. I do not knowingly collect personally identifiable information from anyone under 18. In the case I discover that a minor under 18 has provided me with personal information, I immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact me so that I will be able to do necessary actions.\n" +
                                    "\n" +
                                    "Changes to This Privacy Policy\n" +
                                    "\n" +
                                    "I may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Privacy Policy on this page. These changes are effective immediately after they are posted on this page.\n" +
                                    "\n" +
                                    "Contact Us\n" +
                                    "\n" +
                                    "If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me.");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadSpanishValues() {
        try {
            spanishObject = new JSONObject();
            spanishObject.put("PRIVACY_POLICY", "Pol??tica de Privacidad");
            spanishObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino cre?? la aplicaci??n HTTP REST como una aplicaci??n gratuita. Este SERVICIO es provisto por Haroldo Paulino sin costo y est?? destinado a ser utilizado tal cual.\n" +
                            "\n" +
                            "Esta pol??tica se utiliza para informar a los visitantes de la Aplicaci??n sobre mis pol??ticas con la recopilaci??n, el uso y la divulgaci??n de Informaci??n personal si alguien decide utilizar mi Servicio.\n" +
                            "\n" +
                            "Si elige usar mi Servicio, acepta la recopilaci??n y el uso de la informaci??n en relaci??n con esta pol??tica. La informaci??n personal que recopilo se utiliza para proporcionar y mejorar el servicio. No utilizar?? ni compartir?? su informaci??n con nadie, excepto como se describe en esta Pol??tica de privacidad.\n" +
                            "\n" +
                            "Los t??rminos utilizados en esta Pol??tica de privacidad tienen los mismos significados que en nuestros T??rminos y condiciones, a los que se puede acceder en las cotizaciones de Papi, a menos que se establezca lo contrario en esta Pol??tica de privacidad.\n" +
                            "\n" +
                            "Recopilaci??n y uso de informaci??n\n" +
                            "\n" +
                            "Para una mejor experiencia, al utilizar nuestro Servicio, es posible que le pidamos que nos brinde cierta informaci??n de identificaci??n personal, que incluye, pero no se limita a, Acceso al Estado del dispositivo y Contactos. La informaci??n que solicito se conserva en su dispositivo y no la recopilo de ninguna manera\n" +
                            "\n" +
                            "La aplicaci??n utiliza servicios de terceros que pueden recopilar informaci??n utilizada para identificarlo.\n" +
                            "\n" +
                            "Enlace a la pol??tica de privacidad de proveedores de servicios de terceros utilizados por la aplicaci??n\n" +
                            "\n" +
                            "Servicios de Google Play\n" +
                            "Dato de registro\n" +
                            "\n" +
                            "Deseo informarle que cada vez que utiliza mi Servicio, en caso de error en la aplicaci??n, recopilo datos e informaci??n (a trav??s de productos de terceros) en su tel??fono llamados Datos de registro. Este registro de datos puede incluir informaci??n como la direcci??n del protocolo de Internet (\"IP\") del dispositivo, el nombre del dispositivo, la versi??n del sistema operativo, la configuraci??n de la aplicaci??n al utilizar mi servicio, la hora y fecha de uso del servicio y otras estad??sticas .\n" +
                            "\n" +
                            "Proveedores de servicio\n" +
                            "\n" +
                            "Puedo emplear compa????as e individuos de terceros debido a las siguientes razones:\n" +
                            "\n" +
                            "* Para facilitar nuestro Servicio;\n" +
                            "* Para proporcionar el Servicio en nuestro nombre;\n" +
                            "* Para realizar servicios relacionados con el servicio; o\n" +
                            "* Para ayudarnos a analizar c??mo se utiliza nuestro Servicio.\n" +
                            "* Deseo informar a los usuarios de este Servicio que estos terceros tienen acceso a su Informaci??n personal. El motivo es realizar las tareas que se les asignaron en nuestro nombre. Sin embargo, est??n obligados a no divulgar ni utilizar la informaci??n para ning??n otro fin.\n" +
                            "\n" +
                            "Seguridad\n" +
                            "\n" +
                            "Valoro su confianza al proporcionarnos su Informaci??n personal, por lo tanto, nos esforzamos por utilizar medios comercialmente aceptables para protegerla. Pero recuerde que ning??n m??todo de transmisi??n a trav??s de Internet o m??todo de almacenamiento electr??nico es 100% seguro y confiable, y no puedo garantizar su absoluta seguridad.\n" +
                            "\n" +
                            "Enlaces a otros sitios\n" +
                            "\n" +
                            "Este Servicio puede contener enlaces a otros sitios. Si hace clic en un enlace de un tercero, se lo dirigir?? a ese sitio. Tenga en cuenta que estos sitios externos no son operados por m??. Por lo tanto, le recomiendo encarecidamente que revise la Pol??tica de privacidad de estos sitios web. No tengo control ni asumo ninguna responsabilidad por el contenido, las pol??ticas de privacidad o las pr??cticas de sitios o servicios de terceros.\n" +
                            "\n" +
                            "Privacidad de los ni??os\n" +
                            "\n" +
                            "Estos Servicios no se dirigen a personas menores de 18 a??os. No recopil?? a sabiendas informaci??n personal identificable de ni??os menores de 18 a??os. En caso de que descubra que un ni??o menor de 18 a??os me ha proporcionado informaci??n personal, inmediatamente la borro de nuestros servidores. Si usted es un padre o tutor y sabe que su hijo nos ha proporcionado informaci??n personal, cont??cteme para que yo pueda hacer las acciones necesarias.\n" +
                            "\n" +
                            "Cambios a esta pol??tica de privacidad\n" +
                            "\n" +
                            "Es posible que actualice nuestra Pol??tica de privacidad de vez en cuando. Por lo tanto, se recomienda revisar esta p??gina peri??dicamente para cualquier cambio. Le notificar?? cualquier cambio mediante la publicaci??n de la nueva Pol??tica de Privacidad en esta p??gina. Estos cambios entran en vigencia inmediatamente despu??s de que se publiquen en esta p??gina.\n" +
                            "\n" +
                            "Cont??ctenos\n" +
                            "\n" +
                            "Si tiene alguna pregunta o sugerencia sobre mi Pol??tica de privacidad, no dude en ponerse en contacto conmigo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadFrenchValues() {
        try {
            frenchObject = new JSONObject();
            frenchObject.put("PRIVACY_POLICY", "Politique de Confidentialit??");
            frenchObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino a cr???? l'application HTTP REST en tant qu'application gratuite. Ce SERVICE est fourni par Haroldo Paulino sans frais et est destin?? ?? ??tre utilis?? tel quel.\n" +
                            "\n" +
                            "Cette politique est utilis??e pour informer les visiteurs de l'application de mes politiques en mati??re de collecte, d'utilisation et de divulgation des informations personnelles si quelqu'un a d??cid?? d'utiliser mon service.\n" +
                            "\n" +
                            "Si vous choisissez d'utiliser mon service, vous acceptez la collecte et l'utilisation des informations relatives ?? cette politique. Les informations personnelles que je recueille sont utilis??es pour fournir et am??liorer le service. Je n'utiliserai ni ne partagerai vos informations avec quiconque, sauf comme d??crit dans cette politique de confidentialit??.\n" +
                            "\n" +
                            "Les termes utilis??s dans cette politique de confidentialit?? ont les m??mes significations que dans nos conditions g??n??rales, qui sont accessibles sur HTTP REST, sauf indication contraire dans la pr??sente politique de confidentialit??.\n" +
                            "\n" +
                            "Collecte d'informations et utilisation\n" +
                            "\n" +
                            "Pour une meilleure exp??rience, tout en utilisant notre Service, je peux exiger que vous nous fournissiez certaines informations personnellement identifiables, y compris, entre autres, l'acc??s ?? l'??tat du p??riph??rique et aux contacts. Les informations que je demande sont conserv??es sur votre appareil et ne sont en aucun cas collect??es.\n" +
                            "\n" +
                            "L'application utilise des services tiers pouvant collecter des informations permettant de vous identifier.\n" +
                            "\n" +
                            "Lien vers la politique de confidentialit?? des fournisseurs de services tiers utilis??s par l'application\n" +
                            "\n" +
                            "Services Google Play\n" +
                            "Donn??es de journal\n" +
                            "\n" +
                            "Je souhaite vous informer que chaque fois que vous utilisez mon service, en cas d'erreur dans l'application, je collecte des donn??es et des informations (via des produits tiers) sur votre t??l??phone, appel??es donn??es de journal. Ces donn??es de journal peuvent inclure des informations telles que l'adresse IP de votre appareil, le nom de l'appareil, la version du syst??me d'exploitation, la configuration de l'application lors de l'utilisation de mon service, l'heure et la date d'utilisation du service et d'autres statistiques. .\n" +
                            "\n" +
                            "Les fournisseurs de services\n" +
                            "\n" +
                            "Je peux employer des soci??t??s tierces et des particuliers pour les raisons suivantes:\n" +
                            "\n" +
                            "* Pour faciliter notre service;\n" +
                            "* Fournir le service en notre nom;\n" +
                            "* Effectuer des services li??s au service; ou\n" +
                            "* Pour nous aider ?? analyser l'utilisation de notre service.\n" +
                            "* Je souhaite informer les utilisateurs de ce service que ces tiers ont acc??s ?? vos informations personnelles. La raison est d'effectuer les t??ches qui leur sont assign??es en notre nom. Cependant, ils sont tenus de ne pas divulguer ou utiliser les informations ?? d'autres fins.\n" +
                            "\n" +
                            "S??curit??\n" +
                            "\n" +
                            "J'appr??cie votre confiance ?? nous fournir vos informations personnelles, nous nous effor??ons donc d'utiliser des moyens commercialement acceptables pour le prot??ger. Mais rappelez-vous qu'aucune m??thode de transmission sur Internet ou m??thode de stockage ??lectronique n'est s??curis??e et fiable ?? 100%, et je ne peux garantir sa s??curit?? absolue.\n" +
                            "\n" +
                            "Liens vers d'autres sites\n" +
                            "\n" +
                            "Ce service peut contenir des liens vers d'autres sites. Si vous cliquez sur un lien tiers, vous serez redirig?? vers ce site. Notez que ces sites externes ne sont pas exploit??s par moi. Par cons??quent, je vous conseille vivement de consulter la politique de confidentialit?? de ces sites Web. Je n'ai aucun contr??le et n'assume aucune responsabilit?? pour le contenu, les politiques de confidentialit?? ou les pratiques de tout site ou service tiers.\n" +
                            "\n" +
                            "La vie priv??e des enfants\n" +
                            "\n" +
                            "Ces services ne s'adressent ?? personne de moins de 18 ans. Je ne recueille pas sciemment des informations personnellement identifiables aupr??s d'enfants de moins de 18 ans. Si je d??couvre qu'un enfant de moins de 18 ans m'a fourni des informations personnelles, je les supprime imm??diatement de nos serveurs. Si vous ??tes un parent ou un tuteur et que vous savez que votre enfant nous a fourni des informations personnelles, contactez-moi pour que je puisse faire les actions n??cessaires.\n" +
                            "\n" +
                            "Changements ?? cette politique de confidentialit??\n" +
                            "\n" +
                            "Je peux mettre ?? jour notre politique de confidentialit?? de temps ?? autre. Ainsi, il est conseill?? de consulter cette page p??riodiquement pour toute modification. Je vous informerai de tout changement en publiant la nouvelle politique de confidentialit?? sur cette page. Ces modifications prennent effet imm??diatement apr??s leur publication sur cette page.\n" +
                            "\n" +
                            "Contactez nous\n" +
                            "\n" +
                            "Si vous avez des questions ou des suggestions concernant ma politique de confidentialit??, n'h??sitez pas ?? me contacter.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadPortugueseValues() {
        try {
            portugueseObject = new JSONObject();
            portugueseObject.put("PRIVACY_POLICY", "Pol??tica de Privacidade");
            portugueseObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino construiu o aplicativo HTTP REST como um aplicativo gratuito. Este servi??o ?? fornecido por Haroldo Paulino sem nenhum custo e ?? destinado para uso como ??.\n" +
                            "\n" +
                            "Esta pol??tica ?? usada para informar os visitantes do Aplicativo sobre minhas pol??ticas com a coleta, uso e divulga????o de Informa????es Pessoais, caso algu??m decida usar meu Servi??o.\n" +
                            "\n" +
                            "Se voc?? optar por usar o meu Servi??o, voc?? concorda com a coleta e uso de informa????es em rela????o a esta pol??tica. As Informa????es Pessoais que eu coleciono s??o usadas para fornecer e melhorar o Servi??o. N??o usarei nem compartilharei suas informa????es com ningu??m, exceto conforme descrito nesta Pol??tica de Privacidade.\n" +
                            "\n" +
                            "Os termos usados \u200B\u200Bnesta Pol??tica de Privacidade t??m os mesmos significados que em nossos Termos e Condi????es, que podem ser acessados \u200B\u200Bem Cota????es da Papi, a menos que definido de outra forma nesta Pol??tica de Privacidade.\n" +
                            "\n" +
                            "Recolha e Uso de Informa????o\n" +
                            "\n" +
                            "Para uma melhor experi??ncia, ao usar nosso Servi??o, eu posso exigir que voc?? nos forne??a algumas informa????es pessoalmente identific??veis, incluindo, mas n??o se limitando a, Acesso ao Estado do Dispositivo e Contatos. As informa????es que eu solicito s??o retidas no seu dispositivo e n??o s??o coletadas por mim de maneira alguma\n" +
                            "\n" +
                            "O aplicativo usa servi??os de terceiros que podem coletar informa????es usadas para identific??-lo.\n" +
                            "\n" +
                            "Link para a pol??tica de privacidade de provedores de servi??os de terceiros usados \u200B\u200Bpelo aplicativo\n" +
                            "\n" +
                            "Google Play Services\n" +
                            "Dados de Log\n" +
                            "\n" +
                            "Quero inform??-lo que sempre que voc?? usar o meu servi??o, em caso de erro no aplicativo eu coletar dados e informa????es (atrav??s de produtos de terceiros) em seu telefone chamado Log Data. Esses dados de registro podem incluir informa????es como o endere??o IP do dispositivo, o nome do dispositivo, a vers??o do sistema operacional, a configura????o do aplicativo ao utilizar meu servi??o, a hora e a data do seu uso do Servi??o e outras estat??sticas. .\n" +
                            "\n" +
                            "Provedores de servi??o\n" +
                            "\n" +
                            "Eu posso empregar empresas e indiv??duos de terceiros devido ??s seguintes raz??es:\n" +
                            "\n" +
                            "* Facilitar nosso servi??o;\n" +
                            "* Para fornecer o servi??o em nosso nome;\n" +
                            "* Para executar servi??os relacionados a servi??os; ou\n" +
                            "* Para nos ajudar a analisar como nosso Servi??o ?? usado.\n" +
                            "* Quero informar aos usu??rios deste Servi??o que esses terceiros tenham acesso ??s suas Informa????es Pessoais. O motivo ?? executar as tarefas atribu??das a eles em nosso nome. No entanto, eles s??o obrigados a n??o divulgar ou usar as informa????es para qualquer outra finalidade.\n" +
                            "\n" +
                            "Seguran??a\n" +
                            "\n" +
                            "Eu valorizo \u200B\u200Bsua confian??a em nos fornecer suas Informa????es Pessoais, por isso estamos nos esfor??ando para usar meios comercialmente aceit??veis \u200B\u200Bde proteg??-los. Mas lembre-se de que nenhum m??todo de transmiss??o pela Internet ou m??todo de armazenamento eletr??nico ?? 100% seguro e confi??vel, e n??o posso garantir sua seguran??a absoluta.\n" +
                            "\n" +
                            "Links para outros sites\n" +
                            "\n" +
                            "Este Servi??o pode conter links para outros sites. Se voc?? clicar em um link de terceiros, voc?? ser?? direcionado para esse site. Observe que esses sites externos n??o s??o operados por mim. Portanto, aconselho vivamente que voc?? revise a Pol??tica de Privacidade desses sites. N??o tenho controle e n??o me responsabilizo pelo conte??do, pol??ticas de privacidade ou pr??ticas de sites ou servi??os de terceiros.\n" +
                            "\n" +
                            "Privacidade infantil\n" +
                            "\n" +
                            "Esses Servi??os n??o abordam ningu??m com idade inferior a 18 anos. N??o recolho intencionalmente informa????es de identifica????o pessoal de crian??as com menos de 18 anos. No caso de descobrir que uma crian??a com menos de 18 anos me forneceu informa????es pessoais, excluo imediatamente isso de nossos servidores. Se voc?? ?? pai / m??e ou respons??vel legal e sabe que seu filho nos forneceu informa????es pessoais, entre em contato comigo para que eu possa tomar as provid??ncias necess??rias.\n" +
                            "\n" +
                            "Altera????es a esta pol??tica de privacidade\n" +
                            "\n" +
                            "Eu posso atualizar nossa Pol??tica de Privacidade de tempos em tempos. Assim, aconselhamos que voc?? revise esta p??gina periodicamente para quaisquer altera????es. Vou notific??-lo de quaisquer altera????es publicando a nova Pol??tica de Privacidade nesta p??gina. Estas altera????es entram em vigor imediatamente ap??s serem publicadas nesta p??gina.\n" +
                            "\n" +
                            "Contate-Nos\n" +
                            "\n" +
                            "Se voc?? tiver d??vidas ou sugest??es sobre minha Pol??tica de Privacidade, n??o hesite em entrar em contato comigo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
