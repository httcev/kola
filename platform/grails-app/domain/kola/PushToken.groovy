package kola

class PushToken {
    static belongsTo = User
    static mapping = {
        id column: "user_id"
    }
 
    String token
    Date lastUpdated
}
