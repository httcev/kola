package kola

class Settings {
    boolean singleEntry = true
    static constraints = {
         singleEntry nullable: false, validator: { val, obj ->
              if(val && obj.id != getSettings()?.id && Settings.count > 0) {
                    return "Settings already exists in database"
              }
         }
    }

    static Settings getSettings(){
         Settings.first()
    }

    String welcomeHeader = "Willkommen"
    String welcomeBody = "Dies ist die KOLA Plattform."
}