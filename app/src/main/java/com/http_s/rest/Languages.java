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
                                    "I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.\n" +
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
                                    "Children’s Privacy\n" +
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
            spanishObject.put("PRIVACY_POLICY", "Política de Privacidad");
            spanishObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino creó la aplicación HTTP REST como una aplicación gratuita. Este SERVICIO es provisto por Haroldo Paulino sin costo y está destinado a ser utilizado tal cual.\n" +
                            "\n" +
                            "Esta política se utiliza para informar a los visitantes de la Aplicación sobre mis políticas con la recopilación, el uso y la divulgación de Información personal si alguien decide utilizar mi Servicio.\n" +
                            "\n" +
                            "Si elige usar mi Servicio, acepta la recopilación y el uso de la información en relación con esta política. La información personal que recopilo se utiliza para proporcionar y mejorar el servicio. No utilizaré ni compartiré su información con nadie, excepto como se describe en esta Política de privacidad.\n" +
                            "\n" +
                            "Los términos utilizados en esta Política de privacidad tienen los mismos significados que en nuestros Términos y condiciones, a los que se puede acceder en las cotizaciones de Papi, a menos que se establezca lo contrario en esta Política de privacidad.\n" +
                            "\n" +
                            "Recopilación y uso de información\n" +
                            "\n" +
                            "Para una mejor experiencia, al utilizar nuestro Servicio, es posible que le pidamos que nos brinde cierta información de identificación personal, que incluye, pero no se limita a, Acceso al Estado del dispositivo y Contactos. La información que solicito se conserva en su dispositivo y no la recopilo de ninguna manera\n" +
                            "\n" +
                            "La aplicación utiliza servicios de terceros que pueden recopilar información utilizada para identificarlo.\n" +
                            "\n" +
                            "Enlace a la política de privacidad de proveedores de servicios de terceros utilizados por la aplicación\n" +
                            "\n" +
                            "Servicios de Google Play\n" +
                            "Dato de registro\n" +
                            "\n" +
                            "Deseo informarle que cada vez que utiliza mi Servicio, en caso de error en la aplicación, recopilo datos e información (a través de productos de terceros) en su teléfono llamados Datos de registro. Este registro de datos puede incluir información como la dirección del protocolo de Internet (\"IP\") del dispositivo, el nombre del dispositivo, la versión del sistema operativo, la configuración de la aplicación al utilizar mi servicio, la hora y fecha de uso del servicio y otras estadísticas .\n" +
                            "\n" +
                            "Proveedores de servicio\n" +
                            "\n" +
                            "Puedo emplear compañías e individuos de terceros debido a las siguientes razones:\n" +
                            "\n" +
                            "* Para facilitar nuestro Servicio;\n" +
                            "* Para proporcionar el Servicio en nuestro nombre;\n" +
                            "* Para realizar servicios relacionados con el servicio; o\n" +
                            "* Para ayudarnos a analizar cómo se utiliza nuestro Servicio.\n" +
                            "* Deseo informar a los usuarios de este Servicio que estos terceros tienen acceso a su Información personal. El motivo es realizar las tareas que se les asignaron en nuestro nombre. Sin embargo, están obligados a no divulgar ni utilizar la información para ningún otro fin.\n" +
                            "\n" +
                            "Seguridad\n" +
                            "\n" +
                            "Valoro su confianza al proporcionarnos su Información personal, por lo tanto, nos esforzamos por utilizar medios comercialmente aceptables para protegerla. Pero recuerde que ningún método de transmisión a través de Internet o método de almacenamiento electrónico es 100% seguro y confiable, y no puedo garantizar su absoluta seguridad.\n" +
                            "\n" +
                            "Enlaces a otros sitios\n" +
                            "\n" +
                            "Este Servicio puede contener enlaces a otros sitios. Si hace clic en un enlace de un tercero, se lo dirigirá a ese sitio. Tenga en cuenta que estos sitios externos no son operados por mí. Por lo tanto, le recomiendo encarecidamente que revise la Política de privacidad de estos sitios web. No tengo control ni asumo ninguna responsabilidad por el contenido, las políticas de privacidad o las prácticas de sitios o servicios de terceros.\n" +
                            "\n" +
                            "Privacidad de los niños\n" +
                            "\n" +
                            "Estos Servicios no se dirigen a personas menores de 18 años. No recopilé a sabiendas información personal identificable de niños menores de 18 años. En caso de que descubra que un niño menor de 18 años me ha proporcionado información personal, inmediatamente la borro de nuestros servidores. Si usted es un padre o tutor y sabe que su hijo nos ha proporcionado información personal, contácteme para que yo pueda hacer las acciones necesarias.\n" +
                            "\n" +
                            "Cambios a esta política de privacidad\n" +
                            "\n" +
                            "Es posible que actualice nuestra Política de privacidad de vez en cuando. Por lo tanto, se recomienda revisar esta página periódicamente para cualquier cambio. Le notificaré cualquier cambio mediante la publicación de la nueva Política de Privacidad en esta página. Estos cambios entran en vigencia inmediatamente después de que se publiquen en esta página.\n" +
                            "\n" +
                            "Contáctenos\n" +
                            "\n" +
                            "Si tiene alguna pregunta o sugerencia sobre mi Política de privacidad, no dude en ponerse en contacto conmigo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadFrenchValues() {
        try {
            frenchObject = new JSONObject();
            frenchObject.put("PRIVACY_POLICY", "Politique de Confidentialité");
            frenchObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino a créé l'application HTTP REST en tant qu'application gratuite. Ce SERVICE est fourni par Haroldo Paulino sans frais et est destiné à être utilisé tel quel.\n" +
                            "\n" +
                            "Cette politique est utilisée pour informer les visiteurs de l'application de mes politiques en matière de collecte, d'utilisation et de divulgation des informations personnelles si quelqu'un a décidé d'utiliser mon service.\n" +
                            "\n" +
                            "Si vous choisissez d'utiliser mon service, vous acceptez la collecte et l'utilisation des informations relatives à cette politique. Les informations personnelles que je recueille sont utilisées pour fournir et améliorer le service. Je n'utiliserai ni ne partagerai vos informations avec quiconque, sauf comme décrit dans cette politique de confidentialité.\n" +
                            "\n" +
                            "Les termes utilisés dans cette politique de confidentialité ont les mêmes significations que dans nos conditions générales, qui sont accessibles sur HTTP REST, sauf indication contraire dans la présente politique de confidentialité.\n" +
                            "\n" +
                            "Collecte d'informations et utilisation\n" +
                            "\n" +
                            "Pour une meilleure expérience, tout en utilisant notre Service, je peux exiger que vous nous fournissiez certaines informations personnellement identifiables, y compris, entre autres, l'accès à l'état du périphérique et aux contacts. Les informations que je demande sont conservées sur votre appareil et ne sont en aucun cas collectées.\n" +
                            "\n" +
                            "L'application utilise des services tiers pouvant collecter des informations permettant de vous identifier.\n" +
                            "\n" +
                            "Lien vers la politique de confidentialité des fournisseurs de services tiers utilisés par l'application\n" +
                            "\n" +
                            "Services Google Play\n" +
                            "Données de journal\n" +
                            "\n" +
                            "Je souhaite vous informer que chaque fois que vous utilisez mon service, en cas d'erreur dans l'application, je collecte des données et des informations (via des produits tiers) sur votre téléphone, appelées données de journal. Ces données de journal peuvent inclure des informations telles que l'adresse IP de votre appareil, le nom de l'appareil, la version du système d'exploitation, la configuration de l'application lors de l'utilisation de mon service, l'heure et la date d'utilisation du service et d'autres statistiques. .\n" +
                            "\n" +
                            "Les fournisseurs de services\n" +
                            "\n" +
                            "Je peux employer des sociétés tierces et des particuliers pour les raisons suivantes:\n" +
                            "\n" +
                            "* Pour faciliter notre service;\n" +
                            "* Fournir le service en notre nom;\n" +
                            "* Effectuer des services liés au service; ou\n" +
                            "* Pour nous aider à analyser l'utilisation de notre service.\n" +
                            "* Je souhaite informer les utilisateurs de ce service que ces tiers ont accès à vos informations personnelles. La raison est d'effectuer les tâches qui leur sont assignées en notre nom. Cependant, ils sont tenus de ne pas divulguer ou utiliser les informations à d'autres fins.\n" +
                            "\n" +
                            "Sécurité\n" +
                            "\n" +
                            "J'apprécie votre confiance à nous fournir vos informations personnelles, nous nous efforçons donc d'utiliser des moyens commercialement acceptables pour le protéger. Mais rappelez-vous qu'aucune méthode de transmission sur Internet ou méthode de stockage électronique n'est sécurisée et fiable à 100%, et je ne peux garantir sa sécurité absolue.\n" +
                            "\n" +
                            "Liens vers d'autres sites\n" +
                            "\n" +
                            "Ce service peut contenir des liens vers d'autres sites. Si vous cliquez sur un lien tiers, vous serez redirigé vers ce site. Notez que ces sites externes ne sont pas exploités par moi. Par conséquent, je vous conseille vivement de consulter la politique de confidentialité de ces sites Web. Je n'ai aucun contrôle et n'assume aucune responsabilité pour le contenu, les politiques de confidentialité ou les pratiques de tout site ou service tiers.\n" +
                            "\n" +
                            "La vie privée des enfants\n" +
                            "\n" +
                            "Ces services ne s'adressent à personne de moins de 18 ans. Je ne recueille pas sciemment des informations personnellement identifiables auprès d'enfants de moins de 18 ans. Si je découvre qu'un enfant de moins de 18 ans m'a fourni des informations personnelles, je les supprime immédiatement de nos serveurs. Si vous êtes un parent ou un tuteur et que vous savez que votre enfant nous a fourni des informations personnelles, contactez-moi pour que je puisse faire les actions nécessaires.\n" +
                            "\n" +
                            "Changements à cette politique de confidentialité\n" +
                            "\n" +
                            "Je peux mettre à jour notre politique de confidentialité de temps à autre. Ainsi, il est conseillé de consulter cette page périodiquement pour toute modification. Je vous informerai de tout changement en publiant la nouvelle politique de confidentialité sur cette page. Ces modifications prennent effet immédiatement après leur publication sur cette page.\n" +
                            "\n" +
                            "Contactez nous\n" +
                            "\n" +
                            "Si vous avez des questions ou des suggestions concernant ma politique de confidentialité, n'hésitez pas à me contacter.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadPortugueseValues() {
        try {
            portugueseObject = new JSONObject();
            portugueseObject.put("PRIVACY_POLICY", "Política de Privacidade");
            portugueseObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino construiu o aplicativo HTTP REST como um aplicativo gratuito. Este serviço é fornecido por Haroldo Paulino sem nenhum custo e é destinado para uso como é.\n" +
                            "\n" +
                            "Esta política é usada para informar os visitantes do Aplicativo sobre minhas políticas com a coleta, uso e divulgação de Informações Pessoais, caso alguém decida usar meu Serviço.\n" +
                            "\n" +
                            "Se você optar por usar o meu Serviço, você concorda com a coleta e uso de informações em relação a esta política. As Informações Pessoais que eu coleciono são usadas para fornecer e melhorar o Serviço. Não usarei nem compartilharei suas informações com ninguém, exceto conforme descrito nesta Política de Privacidade.\n" +
                            "\n" +
                            "Os termos usados \u200B\u200Bnesta Política de Privacidade têm os mesmos significados que em nossos Termos e Condições, que podem ser acessados \u200B\u200Bem Cotações da Papi, a menos que definido de outra forma nesta Política de Privacidade.\n" +
                            "\n" +
                            "Recolha e Uso de Informação\n" +
                            "\n" +
                            "Para uma melhor experiência, ao usar nosso Serviço, eu posso exigir que você nos forneça algumas informações pessoalmente identificáveis, incluindo, mas não se limitando a, Acesso ao Estado do Dispositivo e Contatos. As informações que eu solicito são retidas no seu dispositivo e não são coletadas por mim de maneira alguma\n" +
                            "\n" +
                            "O aplicativo usa serviços de terceiros que podem coletar informações usadas para identificá-lo.\n" +
                            "\n" +
                            "Link para a política de privacidade de provedores de serviços de terceiros usados \u200B\u200Bpelo aplicativo\n" +
                            "\n" +
                            "Google Play Services\n" +
                            "Dados de Log\n" +
                            "\n" +
                            "Quero informá-lo que sempre que você usar o meu serviço, em caso de erro no aplicativo eu coletar dados e informações (através de produtos de terceiros) em seu telefone chamado Log Data. Esses dados de registro podem incluir informações como o endereço IP do dispositivo, o nome do dispositivo, a versão do sistema operacional, a configuração do aplicativo ao utilizar meu serviço, a hora e a data do seu uso do Serviço e outras estatísticas. .\n" +
                            "\n" +
                            "Provedores de serviço\n" +
                            "\n" +
                            "Eu posso empregar empresas e indivíduos de terceiros devido às seguintes razões:\n" +
                            "\n" +
                            "* Facilitar nosso serviço;\n" +
                            "* Para fornecer o serviço em nosso nome;\n" +
                            "* Para executar serviços relacionados a serviços; ou\n" +
                            "* Para nos ajudar a analisar como nosso Serviço é usado.\n" +
                            "* Quero informar aos usuários deste Serviço que esses terceiros tenham acesso às suas Informações Pessoais. O motivo é executar as tarefas atribuídas a eles em nosso nome. No entanto, eles são obrigados a não divulgar ou usar as informações para qualquer outra finalidade.\n" +
                            "\n" +
                            "Segurança\n" +
                            "\n" +
                            "Eu valorizo \u200B\u200Bsua confiança em nos fornecer suas Informações Pessoais, por isso estamos nos esforçando para usar meios comercialmente aceitáveis \u200B\u200Bde protegê-los. Mas lembre-se de que nenhum método de transmissão pela Internet ou método de armazenamento eletrônico é 100% seguro e confiável, e não posso garantir sua segurança absoluta.\n" +
                            "\n" +
                            "Links para outros sites\n" +
                            "\n" +
                            "Este Serviço pode conter links para outros sites. Se você clicar em um link de terceiros, você será direcionado para esse site. Observe que esses sites externos não são operados por mim. Portanto, aconselho vivamente que você revise a Política de Privacidade desses sites. Não tenho controle e não me responsabilizo pelo conteúdo, políticas de privacidade ou práticas de sites ou serviços de terceiros.\n" +
                            "\n" +
                            "Privacidade infantil\n" +
                            "\n" +
                            "Esses Serviços não abordam ninguém com idade inferior a 18 anos. Não recolho intencionalmente informações de identificação pessoal de crianças com menos de 18 anos. No caso de descobrir que uma criança com menos de 18 anos me forneceu informações pessoais, excluo imediatamente isso de nossos servidores. Se você é pai / mãe ou responsável legal e sabe que seu filho nos forneceu informações pessoais, entre em contato comigo para que eu possa tomar as providências necessárias.\n" +
                            "\n" +
                            "Alterações a esta política de privacidade\n" +
                            "\n" +
                            "Eu posso atualizar nossa Política de Privacidade de tempos em tempos. Assim, aconselhamos que você revise esta página periodicamente para quaisquer alterações. Vou notificá-lo de quaisquer alterações publicando a nova Política de Privacidade nesta página. Estas alterações entram em vigor imediatamente após serem publicadas nesta página.\n" +
                            "\n" +
                            "Contate-Nos\n" +
                            "\n" +
                            "Se você tiver dúvidas ou sugestões sobre minha Política de Privacidade, não hesite em entrar em contato comigo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
