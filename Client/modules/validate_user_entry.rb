# Verifica e valida inputs de inteiros, floats e datas do usuário.
# Mais informações em: http://ruby-doc.org/core-2.5.1/Regexp.html
module ValidateUserEntry
    INT_REGEX = /\A\d+\Z/
    
    FLOAT_REGEX = /\A\d+.\d+\Z/

    DATE_REGEX = /\A\d{1,2}-\d{1,2}-\d{4}\Z/
    
end