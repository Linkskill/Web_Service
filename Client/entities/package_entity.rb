# Entidade que representa a base de dados dos pacotes para o lado do cliente e da agência.
class PackageEntity
    attr_reader :id
    attr_reader :ticket_entity
    attr_reader :accommodation_entity
    attr_reader :price
  
    def initialize(package_id: nil, ticket_entity:, accommodation_entity:, price:)
        @id = package_id
        @ticket_entity = ticket_entity
        @accommodation_entity = accommodation_entity
        @price = price
    end
  
    def string_converter
        str = ""

        str += "-----------------------------------\n"
        str += "Oferta de pacote de viagem \##{@id}\n"
        str += "  Tipo da passagem: #{@ticket_entity.ticket_type}\n"
        str += "  Origem: #{@ticket_entity.origin}\n"
        str += "\n"
        str += "  Local de estadia: #{@accommodation_entity.location}\n"
        str += "  Nome do hotel: #{@accommodation_entity.hotel_name}\n"
        str += "  Número de quartos: #{@accommodation_entity.num_rooms}\n"
        str += "  Para: #{@accommodation_entity.num_people} pessoas\n"
        str += "\n"
        str += "  Data de partida: #{@ticket_entity.departure_date.strftime('%d/%m/%Y')}\n"
        str += "  Data de retorno: #{@ticket_entity.return_date.strftime('%d/%m/%Y')}\n"
        str += "  Preço: de #{@ticket_entity.price + accommodation_entity.price} por apenas #{@price}\n"
        str += "-----------------------------------\n"

        return str
    end
  
  end