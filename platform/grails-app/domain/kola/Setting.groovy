package kola

class Setting {
	static mapping = {
		value type: "text"
		weight defaultValue: "1.0", unique:true // must be unique, otherwise we'll possibly get databinding errors (mix ups due to different sorting when rendering vs binding)
	}
	static constraints = {
		key unique:true
		value nullable: true, validator: { val, obj ->
			if(obj.required && !val) {
				return "value for ${obj.key} is required"
			}
		}
	}
	String key
	String value
	String prefix ="settings" // i18n message code prefix
	boolean required
	boolean multiline
	float weight

	static String getValue(key) {
		return Setting.findByKey(key)?.value
	}
}
